# Proxy settings

When behind corporate proxy, you need to confiugure it inside Jenkins:

- Go to Jenkins URL (tipically https://jenkins-hello-dev.apps.../) 
- Go to `Jenkins -> Manage Jenkins -> Manage Plugin -> Advanced pluginManager -> advanced`
- Set proper values and test it with `https://github.com`

*Remember*, jenkins-ephemeral loose every configuration on pod restart.
