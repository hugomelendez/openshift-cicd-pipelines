#!/usr/bin/env groovy

def call(parameters) {
    pipeline {
        agent {
            label parameters.agent
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
                                                credentialsId: "dev-repository-credentials",
                                                remote: "ssh://git@github.com/redhatcsargentina/openshift-cicd-pipelines.git"]))     

                    script {
                        env.APP_NAME = parameters.appName
                        env.IMAGE_NAME = parameters.appName
        
                        env.DEV_PROJECT = "dev"
                        env.TEST_PROJECT = "test"
                        env.PROD_PROJECT = "prod"
                                        
                        env.APP_TEMPLATE = (parameters.template) ? parameters.template : "./openshift/template.yaml"
                        env.APP_TEMPLATE_PARAMETERS_DEV = (parameters.templateParametersDev) ? parameters.templateParametersDev : "./openshift/environments/dev/templateParameters.txt"
                        env.APP_TEMPLATE_PARAMETERS_TEST = (parameters.templateParametersTest) ? parameters.templateParametersTest :  "./openshift/environments/test/templateParameters.txt"
                        env.APP_TEMPLATE_PARAMETERS_PROD = (parameters.templateParametersProd) ? parameters.templateParametersProd :  "./openshift/environments/prod/templateParameters.txt"

                        echo env.APP_TEMPLATE_PARAMETERS_DEV
                    }
                }
            }
            stage("Checkout") {
                steps {      
                    script {
                        env.GIT_COMMIT = checkout(scm).GIT_COMMIT
                    }
                }
            }
            /*
            stage("Compile") {
                steps {
                    sh parameters.compileCommands
                }
            }
            stage("Test") {
                steps {
                    sh parameters.testCommands
                }
            }
            stage("Build Image") {
                steps {
                    applyTemplate(project: env.DEV_PROJECT, 
                                application: env.APP_NAME, 
                                template: env.APP_TEMPLATE, 
                                parameters: env.APP_TEMPLATE_PARAMETERS_DEV,
                                createBuildObjects: true)

                    buildImage(project: env.DEV_PROJECT, 
                            application: env.APP_NAME, 
                            artifactsDir: parameters.artifactsDir)
                }
            }
            stage("Deploy DEV") {
                steps {
                    script {
                        env.TAG_NAME = getVersion(parameters.agent)
                    }   
                    
                    tagImage(srcProject: env.DEV_PROJECT, 
                            srcImage: env.IMAGE_NAME, 
                            srcTag: "latest", 
                            dstProject: env.DEV_PROJECT, 
                            dstImage: env.IMAGE_NAME,
                            dstTag: env.TAG_NAME)
                    
                    deployImage(project: env.DEV_PROJECT, 
                                application: env.APP_NAME, 
                                image: env.IMAGE_NAME, 
                                tag: env.TAG_NAME)
                }
            }
            stage("Deploy TEST") {
                steps {
                    input("Promote to TEST?")

                    applyTemplate(project: env.TEST_PROJECT, 
                                application: env.APP_NAME, 
                                template: env.APP_TEMPLATE, 
                                parameters: env.APP_TEMPLATE_PARAMETERS_TEST)

                    tagImage(srcProject: env.DEV_PROJECT, 
                            srcImage: env.IMAGE_NAME, 
                            srcTag: env.TAG_NAME, 
                            dstProject: env.TEST_PROJECT, 
                            dstImage: env.IMAGE_NAME,
                            dstTag: env.TAG_NAME)
                    
                    deployImage(project: env.TEST_PROJECT, 
                                application: env.APP_NAME, 
                                image: env.IMAGE_NAME, 
                                tag: env.TAG_NAME)
                }
            }
            */
            stage("Integration Test") {
                when {
                    expression {
                        return parameters.integrationTestAgent
                    }
                }
                agent {
                    kubernetes {
                        cloud "openshift"
                        defaultContainer "jnlp"
                        label "${env.APP_NAME}-int-test"
                        yaml readFile(parameters.integrationTestAgent)           
                    }
                }
                steps {
                    checkout(scm)

                    load parameters.integrationTestCommands
                }
            }
            stage("Deploy PROD (Blue)") {
                steps {
                    script {
                        if (!blueGreen.existsBlueGreenRoute(project: env.PROD_PROJECT, application: env.APP_NAME)) {
                            applyTemplate(project: env.PROD_PROJECT, 
                                        application: blueGreen.getApplication1Name(env.APP_NAME), 
                                        template: env.APP_TEMPLATE, 
                                        parameters: env.APP_TEMPLATE_PARAMETERS_PROD)
                                        
                            applyTemplate(project: env.PROD_PROJECT, 
                                        application: blueGreen.getApplication2Name(env.APP_NAME), 
                                        template: env.APP_TEMPLATE, 
                                        parameters: env.APP_TEMPLATE_PARAMETERS_PROD) 

                            blueGreen.createBlueGreenRoute(project: env.PROD_PROJECT, application: env.APP_NAME)
                        } else {
                            applyTemplate(project: env.PROD_PROJECT, 
                                        application: blueGreen.getBlueApplication(project: env.PROD_PROJECT, application: env.APP_NAME), 
                                        template: env.APP_TEMPLATE, 
                                        parameters: env.APP_TEMPLATE_PARAMETERS_PROD)
                        }
                        
                        tagImage(srcProject: env.TEST_PROJECT, 
                                srcImage: env.IMAGE_NAME, 
                                srcTag: env.TAG_NAME, 
                                dstProject: env.PROD_PROJECT, 
                                dstImage: env.IMAGE_NAME,
                                dstTag: env.TAG_NAME)

                        deployImage(project: env.PROD_PROJECT, 
                                    application: blueGreen.getBlueApplication(project: env.PROD_PROJECT, application: env.APP_NAME), 
                                    image: env.IMAGE_NAME, 
                                    tag: env.TAG_NAME)
                    } 
                }
            }
            stage("Deploy PROD (Green)") {
                steps {
                    input("Switch to new version?")

                    script{
                        blueGreen.switchToGreenApplication(project: env.PROD_PROJECT, application: env.APP_NAME)   
                    }              
                }
            }
        }
    }    
}