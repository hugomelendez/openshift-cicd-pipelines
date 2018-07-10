# openshift-pipelines

A common set of pipelines to use in customers.

## Pipelines

* Dev
* Test
* Prod

## Demo

The demo creates **two instances of Minishift** (**Non-Prod** and **Prod** clusters).

The development and test stages are created in the Non-Prod cluster.

The image promotion to production is done with a Skopeo Jenkins slave image.