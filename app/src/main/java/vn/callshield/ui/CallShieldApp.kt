package vn.callshield.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.callshield.data.BlockEntry
import vn.callshield.data.BrandCandidate
import vn.callshield.data.BrandCatalog
import androidx.core.content.edit

private val Ink = Color(0xFF14213D)
private val Blue = Color(0xFF4361EE)
private val Violet = Color(0xFF7209B7)
private val Mint = Color(0xFF6EE7C8)
private val Canvas = Color(0xFFF4F6FC)
@Composable
fun CallShieldApp(viewModel: CallShieldViewModel, onRequestScreeningRole: () -> Unit) {
    val context = LocalContext.current
    viewModel.init(context)
    val entriesFlow = remember { viewModel.entries(context) }
    val entries by entriesFlow.collectAsStateWithLifecycle()
    var brand by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    val prefs = remember(context) { context.getSharedPreferences("callshield_cache", Context.MODE_PRIVATE) }
    var behavior by remember { mutableStateOf(prefs.getString("call_behavior", "notify") ?: "notify") }
    var showBehaviorDialog by remember { mutableStateOf(!prefs.contains("call_behavior")) }
    fun saveBehavior(value: String) {
        behavior = value
        prefs.edit { putString("call_behavior", value) }
        showBehaviorDialog = false
    }
    val matches = remember(search) {
        if (search.isBlank()) emptyList() else BrandCatalog.candidates.filter {
            it.brand.contains(search.trim(), ignoreCase = true) ||
                it.category.contains(search.trim(), ignoreCase = true) || it.phone.contains(search.trim())
        }.distinctBy { "${it.brand}|${it.phone}" }
    }

    Scaffold(
        containerColor = Canvas,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddForm = !showAddForm }, containerColor = Violet, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Thêm số thủ công")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            HeaderCard(entries.count { it.enabled }, viewModel.isScreeningEnabled, onRequestScreeningRole)
            Column(Modifier.fillMaxWidth().background(Canvas).padding(horizontal = 18.dp, vertical = 12.dp)) {
                Text("Tìm thương hiệu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Ink)
                Text("Tìm số đã biết hoặc nhập số bất kỳ của bạn.", color = Color(0xFF68738A), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = search, onValueChange = { search = it; selected = emptySet() },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    placeholder = { Text("Ví dụ: Spam, tư vấn bất động sản…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Cách xử lý số đã thêm", color = Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text(if (behavior == "block") "Chặn và từ chối cuộc gọi" else "Cho phép cuộc gọi, chỉ hiện thông báo", color = Color(0xFF68738A), style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { showBehaviorDialog = true }) { Text("Thay đổi") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (search.isNotBlank()) {
                    if (matches.isEmpty()) {
                        item {
                            InfoCard("Chưa có dữ liệu phù hợp", "Bạn vẫn có thể nhập tên đơn vị và số điện thoại thủ công bằng nút +. Danh mục sẽ tiếp tục được bổ sung khi có nguồn đáng tin cậy.")
                        }
                    } else {
                        item {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Số gợi ý (${matches.size})", fontWeight = FontWeight.Bold, color = Ink)
                                    Text("Chỉ chọn số bạn thực sự muốn chặn.", color = Color(0xFF68738A), style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { selected = if (selected.size == matches.size) emptySet() else matches.map { "${it.brand}|${it.phone}" }.toSet() }) {
                                    Text(if (selected.size == matches.size) "Bỏ chọn" else "Chọn tất cả")
                                }
                            }
                        }
                        items(matches, key = { "${it.brand}|${it.phone}" }) { candidate ->
                            CandidateCard(candidate, selected.contains("${candidate.brand}|${candidate.phone}")) { checked ->
                                val key = "${candidate.brand}|${candidate.phone}"
                                selected = if (checked) selected + key else selected - key
                            }
                        }
                        item {
                            Button(
                                onClick = {
                                    val picked = matches.filter { "${it.brand}|${it.phone}" in selected }
                                    if (picked.isEmpty()) error = "Hãy chọn ít nhất một số."
                                    else {
                                        picked.forEach { candidate -> viewModel.add(context, candidate.brand, candidate.phone, "${candidate.category} • ${candidate.note}") { message -> if (message != null) error = message } }
                                        error = "Đã gửi ${picked.size} số vào danh sách. Hãy kiểm tra trạng thái bên dưới."
                                        selected = emptySet()
                                    }
                                }, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue), shape = RoundedCornerShape(14.dp)
                            ) { Text("Thêm các số đã chọn (${selected.size})", fontWeight = FontWeight.Bold) }
                            Text("Danh mục tham khảo có thể cũ hoặc thay đổi. Hãy xác minh số trên kênh chính thức trước khi chặn; đây không phải kết luận rằng thương hiệu gọi rác.", modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFF68738A), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (showAddForm) {
                    item {
                        Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Thêm số thủ công", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
                                OutlinedTextField(brand, { brand = it }, label = { Text("Thương hiệu / đơn vị") }, placeholder = { Text("Tên đơn vị bất kỳ") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                OutlinedTextField(phone, { phone = it }, label = { Text("Số điện thoại") }, placeholder = { Text("09xxxxxxxx hoặc +84…") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true)
                                OutlinedTextField(note, { note = it }, label = { Text("Ghi chú (không bắt buộc)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                error?.let { Text(it, color = if (it.startsWith("Đã gửi")) Color(0xFF087F5B) else MaterialTheme.colorScheme.error) }
                                Button(onClick = { viewModel.add(context, brand, phone, note) { message -> error = message; if (message == null) { brand = ""; phone = ""; note = ""; showAddForm = false } } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Violet), shape = RoundedCornerShape(14.dp)) { Text("Lưu số điện thoại") }
                            }
                        }
                    }
                }
                item {
                    Column(Modifier.padding(horizontal = 18.dp)) {
                        Text("Danh sách của bạn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Ink)
                        Text("${entries.size} số • ${entries.count { it.enabled }} đang bật chặn", color = Color(0xFF68738A), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (entries.isEmpty()) item { InfoCard("Danh sách đang trống", "Tìm thương hiệu ở phía trên hoặc nhấn nút + để thêm bất kỳ số điện thoại nào.") }
                items(entries, key = { it.id }) { entry -> EntryCard(entry, { viewModel.toggle(context, entry.id, it) }, { viewModel.remove(context, entry.id) }) }
                item { InfoCard("Lưu ý về quyền chặn", "Android thường chỉ chuyển cuộc gọi từ số không có trong danh bạ đến dịch vụ sàng lọc. Nếu số thử nghiệm đã lưu trong danh bạ, ứng dụng có thể không nhận được sự kiện để chặn hoặc gửi thông báo. Không chặn bừa số tổng đài ngân hàng, OTP, giao hàng hay lịch hẹn.") }
            }
        }
    }
    if (showBehaviorDialog) {
        AlertDialog(
            onDismissRequest = { if (prefs.contains("call_behavior")) { behavior = prefs.getString("call_behavior", "notify") ?: "notify"; showBehaviorDialog = false } },
            title = { Text("Bạn muốn xử lý cuộc gọi như thế nào?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Bạn có thể đổi lựa chọn này bất cứ lúc nào.")
                    Row(verticalAlignment = Alignment.Top) {
                        RadioButton(selected = behavior == "notify", onClick = { behavior = "notify" })
                        Column(Modifier.padding(top = 8.dp)) {
                            Text("1. Vẫn hiển thị cuộc gọi", fontWeight = FontWeight.Bold)
                            Text("Cuộc gọi vẫn đến như bình thường; CallShield chỉ gửi thông báo khi số khớp danh sách.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        RadioButton(selected = behavior == "block", onClick = { behavior = "block" })
                        Column(Modifier.padding(top = 8.dp)) {
                            Text("2. Chặn cuộc gọi", fontWeight = FontWeight.Bold)
                            Text("CallShield yêu cầu Android từ chối cuộc gọi từ số đã bật chặn và gửi thông báo nhận diện.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text("Lưu ý: Android/nhà sản xuất có thể giới hạn việc sàng lọc cuộc gọi, đặc biệt với số đã lưu trong danh bạ.", color = Color(0xFF68738A), style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = { TextButton(onClick = { saveBehavior(behavior) }) { Text("Lưu lựa chọn") } }
        )
    }
}
@Composable
private fun HeaderCard(count: Int, screeningEnabled: Boolean, onEnable: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF101B3D), Blue, Violet))).padding(horizontal = 22.dp, vertical = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(Color.White.copy(alpha = .16f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(27.dp)) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("CallShield", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Bộ lọc cuộc gọi không mong muốn", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Tạm biệt làm phiền\nXin chào bình yên.", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f).background(Color.White.copy(alpha = .14f), RoundedCornerShape(14.dp)).padding(10.dp)) { Text(count.toString(), color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge); Text("Số đang chặn", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.bodySmall) }
            Column(Modifier.weight(1f).background(Color.White.copy(alpha = .14f), RoundedCornerShape(14.dp)).padding(10.dp)) { Text(if (screeningEnabled) "Đã bật" else "Chưa bật", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium); Text("Bộ lọc Android", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.bodySmall) }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onEnable, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Ink), shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.PhoneInTalk, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(if (screeningEnabled) "Quyền sàng lọc đã cấp" else "Bật chặn cuộc gọi", fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun CandidateCard(candidate: BrandCandidate, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(17.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = onChecked)
            Column(Modifier.weight(1f)) {
                Text(candidate.brand, color = Ink, fontWeight = FontWeight.Bold)
                Text(candidate.phone, color = Blue, fontWeight = FontWeight.SemiBold)
                Text("${candidate.category} • ${candidate.note}", color = Color(0xFF68738A), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
@Composable
private fun InfoCard(title: String, body: String) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EEFF)), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(title, color = Ink, fontWeight = FontWeight.Bold); Text(body, color = Color(0xFF46536B), style = MaterialTheme.typography.bodyMedium) }
    }
}
@Composable
private fun EntryCard(entry: BlockEntry, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(Color(0xFFF0EDFF), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Security, contentDescription = null, tint = Violet) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.brand, fontWeight = FontWeight.Bold, color = Ink)
                Text(entry.phone, color = Blue, style = MaterialTheme.typography.bodyMedium)
                if (entry.note.isNotBlank()) Text(entry.note, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(if (entry.enabled) "Đang bật chặn" else "Đã tạm dừng", color = if (entry.enabled) Color(0xFFB42318) else Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
            Switch(checked = entry.enabled, onCheckedChange = onToggle)
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Xóa số", tint = Color(0xFF9CA3AF)) }
        }
    }
}