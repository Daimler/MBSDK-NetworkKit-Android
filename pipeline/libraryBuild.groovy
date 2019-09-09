import com.daimler.rs.ModuleDocumentationSpec
import com.daimler.rs.VaultConfig
import com.daimler.rs.VaultParameterSpec

def NODE = 'build-node'

def GIT_URL = 'https://github.com/Daimler/MBSDK-NetworkKit-Android.git'
def MODULE_NAME = 'mbnetworkkit'
def CONFIG = 'Release'
def BRANCH = 'develop'

def approle = 'RIS_VAULT_APPROLE'

node(NODE) {
    def mavenUserSpec = new VaultParameterSpec('app/ris/frontend/artifactory/user', 'user', 'MAVEN_USER')
    def mavenPwSpec = new VaultParameterSpec('app/ris/frontend/artifactory/pw', 'pw', 'MAVEN_PW')
    def azureUserSpec = new VaultParameterSpec('app/ris/ece/prod/azure/devops/username', 'user', 'AZURE_USER')
    def azureTokenSpec = new VaultParameterSpec('app/ris/ece/prod/azure/devops/token', 'token', 'AZURE_TOKEN')
    def vaultConfig = new VaultConfig(approle, 'local.properties', null, null, [mavenUserSpec, mavenPwSpec, azureUserSpec, azureTokenSpec])
    def docsSpec = new ModuleDocumentationSpec('jekyll', '_network_docs', 'gfm', 'modules/network')
    androidPipeline.doStableLibraryBuild(GIT_URL, MODULE_NAME, CONFIG, BRANCH, "$Deploy".toBoolean(), vaultConfig, docsSpec)
}