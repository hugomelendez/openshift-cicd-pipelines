# Image Promotion Approvals

## Steps

See the [Image Promotion Approvals](../../extras/image-promotion-approvals) section.

Create some users and groups to test the approvals:

    oc adm groups new prod-approvers

    oc adm groups add-users prod-approvers admin

The example uses the PROD_APPROVERS_GROUP environment variable and it needs to be set up in Jenkins.
