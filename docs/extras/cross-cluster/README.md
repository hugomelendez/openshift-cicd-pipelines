# Cross-Cluster Image Promotions

## With the CLI (oc image mirror)

The step looks like:

    stage("Promote Image") {
        steps {
            withDockerRegistry([credentialsId: env.SRC_REGISTRY_CREDENTIALS, url: "http://${SRC_REGISTRY_URL}"]) {
                withDockerRegistry([credentialsId: env.DST_REGISTRY_CREDENTIALS, url: "http://${DST_REGISTRY_URL}"]) {
                    script {
                        openshift.withCluster {
                            openshift.raw("image mirror --insecure ${SRC_REGISTRY_URL}/${SRC_PROJECT}/${IMAGE_NAME}:${TAG} ${DST_REGISTRY_URL}/${DST_PROJECT}/${IMAGE_NAME}:${TAG}")
                        }
                    }
                }
            }   
        }
    }

## With Skopeo

Skopeo is a command line utility tool that can manage operations between registries.

An agent with Skopeo installed is required to run the following step of the pipeline. To create the agent see the instructions in the [setup.sh](./skopeo/setup.sh).

The following code snippet represents the Jenkins step:

    stage("Promote Image") {
        agent {
            label "skopeo"
        }
        steps {
            script {
                def srcCreds = "unused:${env.SRC_REGISTRY_TOKEN}";
                def dstCreds = "unused:${env.DST_REGISTRY_TOKEN}";

                def src = "docker://${env.SRC_REGISTRY_URL}/${env.SRC_PROJECT}/${env.IMAGE_NAME}:${env.TAG}";
                def dst = "docker://${env.DST_REGISTRY_URL}/${env.DST_PROJECT}/${env.IMAGE_NAME}:${env.TAG}";

                sh "skopeo copy --src-tls-verify=false --dest-tls-verify=false --src-creds=${srcCreds} --dest-creds=${dstCreds} ${src} ${dst}";
            }
        }
    }