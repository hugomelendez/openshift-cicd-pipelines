library(identifier: "openshift-pipeline-library@master", 
        retriever: modernSCM([$class: "GitSCMSource",
                             credentialsId: "dev-repository-credentials",
                             remote: "ssh://git@github.com/redhatcsargentina/openshift-cicd-pipelines.git"]))

runPipeline(appName: env.APP_NAME,
            agent: "maven",
            artifactsDir: "./deploy",
            compileCommands: "mvn package -DskipTests; mkdir deploy; cp -R ./target/lib ./deploy; p ./target/${env.APP_NAME}-runner.jar ./deploy; cp -R ./.s2i ./deploy",
            testCommands: "mvn test")