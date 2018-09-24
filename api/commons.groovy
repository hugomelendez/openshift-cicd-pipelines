def gitCheckout(repo, branch, secret) {
    def gitInfo = [:]

    gitInfo['url'] = repo

    if (env.GIT_SECRET && !secret.equals("none"))
        gitInfo['credentialsId'] = "${openshift.project()}-${secret}"

    checkout([$class: 'GitSCM', 
             branches: [[name: branch]], 
             doGenerateSubmoduleConfigurations: false, 
             extensions: [], 
             submoduleCfg: [], 
             userRemoteConfigs: [gitInfo]])
}

def buildImage(app, artifactsDir) {
    // If artifacts dir is set, binary s2i build is used
    if (artifactsDir)
        openshift.selector("bc", app).startBuild("--from-dir=${artifactsDir}", "--wait=true")
    else 
        openshift.selector("bc", app).startBuild("--wait=true")       
}

def deployApplication(app, image, tag) {
    if (!openshift.selector("dc", app).exists()) {
        // The creation starts a deployment
        createApplication(app, image, tag)                 
    } else {
        updateApplication(app, image, tag)
    }   

    //verifyDeployment(app)
}

def updateApplication(app, image, tag) {
    openshift.set("triggers", "dc/${app}", "--remove-all")
    openshift.set("triggers", "dc/${app}", "--from-image=${image}:${tag}", "-c ${app}")
}

def createApplication(app, image, tag) {
    // Creates the application and get the brand new BuildConfig
    def dc = openshift.newApp("${image}:${tag}", "--name=${app}").narrow("dc");
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

def getVersion(app) {
    def is = openshift.selector("is", app).object()
    def tags = ""
    
    for (version in is.status.tags)
        tags = version.tag + "\n" + tags
    
    def tag = input(message: "Select version",
                    parameters: [choice(choices: tags, description: 'Select a tag to deploy', name: 'Versions')])

    return tag
}

def resolveApproval(approvalGroups) {
    def submitter = input message: 'Confirm deployment', submitterParameter: 'submitter'
    def user = submitter.substring(0, submitter.lastIndexOf("-"))
    def canApprove = false
    def groups = openshift.selector("groups").objects()
    
    for (g in groups) {
        if (g.metadata.name.equals(approvalGroups) && g.users.contains(user)) {
            canApprove = true
            echo "User ${user} from group ${g.metadata.name} approved the deployment"
        } 
    }

    if (canApprove == false)
        error "User ${user} is not allowed to approve the deployment"
}

return this