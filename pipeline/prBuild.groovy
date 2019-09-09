import com.daimler.rs.VaultConfig
import com.daimler.rs.VaultParameterSpec

def NODE = 'prbuild'

def GIT_URL = 'https://github.com/Daimler/MBSDK-NetworkKit-Android.git'
def MODULE_NAME = 'mbnetworkkit'
def CONFIG = 'release'

def approle = 'RIS_VAULT_APPROLE'

String pr = env.BRANCH_NAME

node(NODE) {
    if (!pr.startsWith('PR-')) {
        return
    }

    def branch = scm.branches

    echo "Checking out PR $pr on branch $branch"

    def mavenUserSpec = new VaultParameterSpec('app/ris/frontend/artifactory/user', 'user', 'MAVEN_USER')
    def mavenPwSpec = new VaultParameterSpec('app/ris/frontend/artifactory/pw', 'pw', 'MAVEN_PW')
    def vaultConfig = new VaultConfig(approle, 'local.properties', null, null, [mavenUserSpec, mavenPwSpec])

    androidPipeline.doPrLibraryBuild(GIT_URL, MODULE_NAME, CONFIG, branch, vaultConfig)
}