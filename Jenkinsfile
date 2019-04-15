pipeline {
    agent {
        label env.TECH
    }
    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }
    stages {
        stage("Initialize") {
            steps {
                library(identifier: "openshift-pipeline-library@master", 
                        retriever: modernSCM([$class: "GitSCMSource", 
                                              remote: "https://github.com/redhatcsargentina/openshift-pipeline-library.git"]))     

                initVars()
            }
        }
        stage("Checkout") {
            steps {                
                gitClone(repository: env.GIT_REPO, 
                         branch: env.GIT_BRANCH, 
                         credentialsId: env.GIT_CREDENTIALS)

                stash env.APPLICATION_OPENSHIFT_FOLDER
            }
        }
        stage("Compile") {
            when {
                expression {
                    return fileExists(env.APPLICATION_COMPILE_COMMAND_SCRIPT)
                }
            }
            steps {
                sh env.APPLICATION_COMPILE_COMMAND_SCRIPT
            }
        }
        stage("Test") {
            steps {
                sh env.APPLICATION_TEST_COMMAND_SCRIPT
            }
        }
        stage("Build Image") {
            steps {
                applyTemplate(project: env.DEV_PROJECT, 
                              application: env.APPLICATION, 
                              template: env.APPLICATION_TEMPLATE_FILE, 
                              parameters: env.APPLICATION_TEMPLATE_FILE_PARAMETERS_DEV,
                              createBuildObjects: true)

                buildImage(project: env.DEV_PROJECT, 
                           application: env.APPLICATION, 
                           artifactsDir: "./artifacts-dir")
            }
        }
        stage("Deploy DEV") {
            steps {
                script {
                    env.TAG = utils.getTag(env.TECH)
                }   
                
                tagImage(srcProject: "${PROJECT}-dev", 
                         srcImage: env.IMAGE, 
                         srcTag: "latest", 
                         dstProject: "${PROJECT}-dev", 
                         dstImage: env.IMAGE,
                         dstTag: env.TAG)
                
                deployImage(project: env.DEV_PROJECT, 
                            application: env.APPLICATION, 
                            image: env.IMAGE, tag: env.TAG)
            }
        }
        stage("Deploy TEST") {
            steps {
                input("Promote to TEST?")

                applyTemplate(project: env.TEST_PROJECT, 
                              application: env.APPLICATION, 
                              template: env.APPLICATION_TEMPLATE_FILE, 
                              parameters: env.APPLICATION_TEMPLATE_FILE_PARAMETERS_TEST)
                
                tagImage(srcProject: env.DEV_PROJECT, 
                         srcImage: env.IMAGE, 
                         srcTag: env.TAG, 
                         dstProject: env.TEST_PROJECT, 
                         dstImage: env.IMAGE,
                         dstTag: env.TAG)
                
                deployImage(project: env.TEST_PROJECT, 
                            application: env.APPLICATION, 
                            image: env.IMAGE, tag: env.TAG)
            }
        }
        stage("Integration Test") {
            agent {
                kubernetes {
                    cloud "openshift"
                    defaultContainer "jnlp"
                    label "${env.APPLICATION}-integration-test"
                    yaml readFile(env.APPLICATION_INTEGRATION_TEST_AGENT)
                }
            }
            steps {
                script {
                    unstash env.APPLICATION_OPENSHIFT_FOLDER
                    
                    def integrationTest = load env.APPLICATION_INTEGRATION_TEST_ENTRYPOINT
                    
                    integrationTest()
                }
            }
        }
        stage("Deploy PROD (Blue)") {
            steps {
                script {
                    if (!blueGreen.existsBlueGreenRoute(project: env.PROD_PROJECT, application: env.APPLICATION)) {
                        applyTemplate(project: env.PROD_PROJECT, 
                                      application: blueGreen.getApplication1Name(env.APPLICATION), 
                                      template: env.APPLICATION_TEMPLATE_FILE, 
                                      parameters: env.APPLICATION_TEMPLATE_FILE_PARAMETERS_PROD)
                                      
                        applyTemplate(project: env.PROD_PROJECT, 
                                      application: blueGreen.getApplication2Name(env.APPLICATION), 
                                      template: env.APPLICATION_TEMPLATE_FILE, 
                                      parameters: env.APPLICATION_TEMPLATE_FILE_PARAMETERS_PROD)

                        blueGreen.createBlueGreenRoute(project: env.PROD_PROJECT, 
                                                       application: env.APPLICATION)
                    }
                    
                    def blueApplication = blueGreen.getBlueApplication(project: env.PROD_PROJECT, application: env.APPLICATION)  

                    applyTemplate(project: env.PROD_PROJECT, 
                                  application: blueApplication, 
                                  template: env.APPLICATION_TEMPLATE_FILE, 
                                  parameters: env.APPLICATION_TEMPLATE_FILE_PARAMETERS_PROD)

                    tagImage(srcProject: env.TEST_PROJECT, 
                             srcImage: env.IMAGE, 
                             srcTag: env.TAG, 
                             dstProject: env.PROD_PROJECT, 
                             dstImage: env.IMAGE,
                             dstTag: env.TAG)

                    deployImage(project: env.PROD_PROJECT, 
                                application: blueApplication, 
                                image: env.IMAGE, tag: env.TAG)
                } 
            }
        }
        stage("Deploy PROD (Green)") {
            steps {
                input("Switch to new version?")

                script{
                    blueGreen.switchToGreenApplication(project: env.PROD_PROJECT, 
                                                       application: env.APPLICATION)   
                }              
            }
        }
    }
}