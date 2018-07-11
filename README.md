# openshift-pipelines

A common set of pipelines to use in customers.

## Pipelines

* [Dev](./dev/README.md)
* Test
* Prod

## Demo

* The demo creates **two instances of Minishift** (**Non-Prod** and **Prod** clusters).
* The development and test stages are created in the Non-Prod cluster.
* The image promotion to production is done with an Skopeo Jenkins slave image.

To create the clusters execute the following command:

    sh demo/setup.sh