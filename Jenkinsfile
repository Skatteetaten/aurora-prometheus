def jenkinsfile
def version='v3.1.0'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    credentialsId: "github",
    deployTo: 'maven-central'
]

jenkinsfile.run(version, overrides)
