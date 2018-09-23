def gitCheckout(repo, branch, secret) {
    
    def gitInfo = [:]

    gitInfo['url'] = repo
    gitInfo['branch'] = branch

    if (env.GIT_SECRET && !secret.equals("none"))
        gitInfo['credentialsId'] = "${openshift.project()}-${secret}"

    git(gitInfo)
    
}

def buildImage(app, artifactsDir) {

    // If artifacts dir is set, binary s2i build is used
    
    if (artifactsDir)
        openshift.selector("bc", app).startBuild("--from-dir=${artifactsDir}", "--wait=true")
    else 
        openshift.selector("bc", app).startBuild("--wait=true")
        
}

def deployApplication(app, tag) {
    openshift.verbose()
    if (!openshift.selector("dc", app).exists()) {
        // The creation starts a deployment
        createApplication(app, tag)                 
    } else {
        updateApplication(app, tag)
    }   

    //verifyDeployment(app)
}

def updateApplication(app, tag) {
    openshift.set("triggers", "dc/${app}", "--remove-all")
    openshift.set("triggers", "dc/${app}", "--from-image=${app}:${tag}", "-c ${app}")
}

def createApplication(app, tag) {
    // Creates the application and get the brand new BuildConfig
    def dc = openshift.newApp("${app}:${tag}").narrow("dc");
    // Creates the app Route
    openshift.selector("svc", app).expose();                
}

def verifyDeployment(app) {
    def dc = openshift.selector("dc", app)

    // Waits for the deployment to finish   
    def latestDeploymentVersion = openshift.selector("dc", app).object().status.latestVersion
    def rc = openshift.selector("rc", "${app}-${latestDeploymentVersion}")
    
    rc.untilEach(1){
        def rcMap = it.object()

        return (rcMap.status.replicas.equals(rcMap.status.readyReplicas))
    }
}

return this