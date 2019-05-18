pipeline {
    agent {
        label "maven"
    }
    options { 
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }
    stages {
        stage("Checkout") {
            steps {
                checkout(scm)
            }
        }
        stage("Compile") {
            steps {
                sh("mvn package -DskipTests")
            }
        }
        stage("Test") {
            steps {
                sh("mvn test")
            }
        }
        stage("Build Image") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            openshift.selector("bc", env.APP_NAME).startBuild("--from-dir=./target", "--wait=true");
                        }
                    }
                }
            }
        }
        stage("Deploy DEV") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            openshift.selector("dc", env.APP_NAME).rollout().status()
                        }
                    }
                }
            }
        }
    }
}