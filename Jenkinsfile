def jenkinsfile
def version='v5'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    credentialsId: "github",
    deployTo: 'maven-central',
    suggestVersionAndTagReleases: [
       [branch: 'master', versionHint: '2']
    ]
]

jenkinsfile.run(version, overrides)
