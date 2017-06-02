def jenkinsfile
def version='v2.10.1'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    library: true,
    deployProperties: "-P sign,build-extras"
]

jenkinsfile.run(version, overrides)