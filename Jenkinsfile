def jenkinsfile
def version='feature/AOS-1592'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    library: true,
    deployProperties: "-P sign,build-extras",
    credentialsId: "github_bjartek",
    mavenSettignsFile: "github-maven-settings"
]

jenkinsfile.run(version, overrides)
