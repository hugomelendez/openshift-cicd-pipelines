library(identifier: "openshift-pipeline-library@master", 
        retriever: modernSCM([$class: "GitSCMSource",
                             credentialsId: "dev-repository-credentials",
                             remote: "ssh://git@github.com/redhatcsargentina/openshift-cicd-pipelines.git"]))

runPipeline(appName: env.APP_NAME,
            agent: "maven",
            compileCommands: "./mvnw package -DskipTests; mkdir deploy; cp -R ./target/lib ./deploy; cp ./target/${env.APP_NAME}-runner.jar ./deploy; cp -R ./.s2i ./deploy",
            testCommands: "./mvnw test",
            artifactsDir: "./deploy")
            //integrationTestAgent: "./openshift/environments/test/integration-test/int-test.yaml",
            //integrationTestCommands: "./openshift/environments/test/integration-test/int-test.groovy")