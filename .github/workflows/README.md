We are (as of 20211221) supporting continuous integration via GitHub actions/workflows, 
which are defined in this folder. See https://docs.github.com/en/actions for full 
documentation of the available features.

We want the following behavior:
- Pushes to a branch trigger build and sonar-scan (branch-build.yml)
- PRs and updates thereto trigger build and sonar-scan (pr-build.yml)
- PRs and updates thereto additionally trigger build, deployment to cloud foundry, execution of 
integration tests, and shutdown of the cloud foundry deployment
- Pushes to develop or main additionally trigger build, deployment to cloud foundry, execution of 
integration tests, and shutdown of the cloud foundry deployment

We have defined branch protection rules requiring success in the above workflows prior to merge to 
develop or main.  

Note:
- These workflows require manual intervention from an admin to run on PRs from forks or from Dependabot
(which is an automatic dependency update tool). See 
https://docs.github.com/en/actions/learn-github-actions/events-that-trigger-workflows#pull-request-events-for-forked-repositories