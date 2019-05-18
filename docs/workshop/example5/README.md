# Example 5 - Pipeline Library

For now on a pipeline library is used for common funcionality in the pipelines.

## Steps

* Create a Git repository for the library (openshift-cicd-pipelines)
* Load the library in Jenkins (for now manual steps)
* Modify the Jenkinsfile to use the library

### Library Steps

* gitClone
* buildImage
* tagImage
* deployImage

See the example [Jenkinsfile](./Jenkinsfile).

