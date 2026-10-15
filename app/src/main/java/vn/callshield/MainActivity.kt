package vn.callshield

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import vn.callshield.ui.CallShieldApp
import vn.callshield.ui.CallShieldViewModel
import vn.callshield.ui.theme.CallShieldTheme
class MainActivity : ComponentActivity() {
    private val viewModel: CallShieldViewModel by viewModels()
    private val notificationPermissionLauncher = registerForActivityResult(RequestPermission()) { }
    private val roleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.refreshScreeningRole(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.refreshScreeningRole(this)
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            CallShieldTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CallShieldApp(
                        viewModel = viewModel,
                        onRequestScreeningRole = { requestScreeningRole() }
                    )
                }
            }
        }
    }
    private fun requestScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            ) {
                roleLauncher.launch(
                    roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                )
            } else {
                viewModel.refreshScreeningRole(this)
            }
        }
    }
}