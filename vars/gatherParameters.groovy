#!/usr/bin/env groovy

def call(parameters) {
    env.APP_NAME = parameters.application
    env.IMAGE_NAME = parameters.application

    env.DEV_PROJECT = "dev"
    env.TEST_PROJECT = "test"
    env.PROD_PROJECT = "prod"
                    
    env.APP_TEMPLATE = (parameters.applicationTemplate) ? parameters.applicationTemplate : "./openshift/template.yaml"
    env.APP_TEMPLATE_PARAMETERS_DEV = (parameters.applicationTemplateParametersDev) ? parameters.applicationTemplateParametersDev : "./openshift/environments/dev/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_TEST = (parameters.applicationTemplateParametersTest) ? parameters.applicationTemplateParametersTest :  "./openshift/environments/test/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_PROD = (parameters.applicationTemplateParametersProd) ? parameters.applicationTemplateParametersProd :  "./openshift/environments/prod/templateParameters.txt"
    env.APP_DEPLOYMENT_PATCH_DEV = (parameters.applicationDeploymentPatchDev) ? parameters.applicationDeploymentPatchDev : "./openshift/environments/dev/deploymentPatch.yaml"
    env.APP_DEPLOYMENT_PATCH_TEST = (parameters.applicationDeploymentPatchTest) ? parameters.applicationDeploymentPatchTest : "./openshift/environments/test/deploymentPatch.yaml"
    env.APP_DEPLOYMENT_PATCH_PROD = (parameters.applicationDeploymentPatchProd) ? parameters.applicationDeploymentPatchProd : "./openshift/environments/prod/deploymentPatch.yaml"
    env.APP_REPLACE_CONFIG_DEV = (parameters.applicationReplaceConfigDev) ? parameters.applicationReplaceConfigDev : "./openshift/environments/dev/replaceConfig.yaml"
    env.APP_REPLACE_CONFIG_TEST = (parameters.applicationReplaceConfigTest) ? parameters.applicationReplaceConfigTest : "./openshift/environments/test/replaceConfig.yaml"
    env.APP_REPLACE_CONFIG_PROD = (parameters.applicationReplaceConfigProd) ? parameters.applicationReplaceConfigProd : "./openshift/environments/prod/replaceConfig.yaml"
}