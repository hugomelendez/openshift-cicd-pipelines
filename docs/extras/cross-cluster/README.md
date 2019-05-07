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

First, the agent **skopeo** needs to be available to use, then the step looks like:

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