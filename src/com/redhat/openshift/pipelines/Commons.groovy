package com.redhat.pipelines

class Commons implements Serializable {
    def script
    def openshift
    def env
    def steps

    Commons(script) {
        this.script = script
        this.openshift = script.openshift
        this.env = script.env
        this.steps = script.steps
    }   

    def processTemplate(template, app, image) {
        return openshift.process(steps.readFile(file: template), "-p", "PARAM_APP_NAME=${app}", "-p", "PARAM_IMAGE_NAME=${image}")
    }

    def processConfig(template, app) {
        return openshift.process(steps.readFile(file: template), "-p", "PARAM_APP_NAME=${app}")
    }

    def applyConfigChanges(config, deploymentPatch) { 
        // Replaces the config map with a new one based on the repository dev config map
        if (steps.fileExists(config))
            openshift.apply(processConfig(config, env.APP_NAME))
        
        // Merges the deployment config with environment specific configuration
        if (steps.fileExists(deploymentPatch))
            patchDeploymentConfig(deploymentPatch, env.APP_NAME)
    }

    def patchDeploymentConfig(deployment, app) {
        // Until OpenShift 3.11, this workaround is necessary: https://github.com/openshift/origin/pull/20456
        try {
            openshift.patch("dc/${app}", "'${steps.readFile(deployment)}'")                            
        } catch (Exception e) { 
            ;
        }
    }

    def hasTemplate(template) {
        if (steps.fileExists(template))
            return true
        return false
    }

    def hasConfig(config) {
        if (steps.fileExists(config))
            return true
        return false
    }

    def hasDeploymentPatch(deploymentPatch) {
        if (steps.fileExists(deploymentPatch))
            return true
        return false
    }

    def processTemplateWithoutBuildObjects(template, app, image) {
        def objects = openshift.process(steps.readFile(file: template), "-p", "PARAM_APP_NAME=${app}", "-p", "PARAM_IMAGE_NAME=${image}")
        def filteredObjects = []

        for (o in objects) {
            // Prevents to promote the BuildConfig and ImageSream, the build is only done in development stages
            if (o.kind != "BuildConfig" && o.kind != "ImageStream") 
                filteredObjects.add(o)
        }

        return filteredObjects
    }

    def getTag(tech) {
        if (tech.equals("java"))
            return steps.readMavenPom().getVersion()
        else if (tech.equals("nodejs")) 
            return getVersionFromPackageJSON()
    }

    def getVersionFromPackageJSON() {
        steps.sh "cat package.json | grep version | head -1 | awk -F: '{ print \$2 }' | sed 's/[\",]//g' | tr -d '[[:space:]]' > version"

        return steps.readFile("version").trim()
    }

    def getApplicationProject() {
        return env.JOB_NAME.split("/")[0]
    }

    def gitCheckout(repo, branch, secret) {
        def gitInfo = [:]

        gitInfo['url'] = repo

        if (env.GIT_SECRET && !secret.equals("none"))
            gitInfo['credentialsId'] = "${openshift.project()}-${secret}"

        steps.checkout([$class: 'GitSCM', 
                       branches: [[name: branch]], 
                       userRemoteConfigs: [gitInfo]])
    }

    def gitCheckout(repo, branch, secret, dir) { 
        def gitInfo = [:]

        def repos = repo.split(".git")

        gitInfo['url'] = "${repos[0]}-config.git"

        if (env.GIT_SECRET && !secret.equals("none"))
            gitInfo['credentialsId'] = "${openshift.project()}-${secret}"

        steps.checkout([$class: 'GitSCM', 
                  branches: [[name: branch]], 
                  extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: dir]], 
                                userRemoteConfigs: [gitInfo]])

    }

    def buildImage(app, image, artifactsDir, baseImage) {
        if (!openshift.selector("bc", "${app}").exists())
            openshift.newBuild("--image-stream=${baseImage}", "--name=${app}", "--binary=true", "-l app=${app}", "--to=${image}");

        openshift.selector("bc", app).startBuild("--from-dir=${artifactsDir}", "--wait=true")
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
        def dc = openshift.selector("dc/${app}").object()

        openshift.set("triggers", "dc/${app}", "--remove-all")
        openshift.set("triggers", "dc/${app}", "--from-image=${image}:${tag}", "-c ${dc.spec.template.spec.containers[0].name}")
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
        
        def tag = steps.input(message: "Select version",
                              parameters: [steps.choice(choices: tags, description: 'Select a tag to deploy', name: 'Versions')])

        return tag
    }

    def resolveApproval(approvalGroups) {
        def submitter = steps.input message: 'Confirm deployment', submitterParameter: 'submitter'
        def user = submitter.substring(0, submitter.lastIndexOf("-"))
        def canApprove = false
        def groups = openshift.selector("groups").objects()
        
        for (g in groups) {
            if (g.metadata.name.equals(approvalGroups) && g.users.contains(user)) {
                canApprove = true
                steps.echo "User ${user} from group ${g.metadata.name} approved the deployment"
            } 
        }

        if (canApprove == false)
            steps.error "User ${user} is not allowed to approve the deployment"
    }
}