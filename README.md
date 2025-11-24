Backstage Developer Portal — CI/CD Self-Service

This Backstage instance provides a self-service deployment workflow.
Developers can trigger Kubernetes deployments by selecting an environment and an image tag.
The deployment flow is executed via Jenkins using a custom Backstage backend action.

## Prerequisites
- npm
- yarn
- docker
- docker compose
- kind

## Install
1. Clone
- git clone https://github.com/Daniel-Elgarisi/devportal-backstage.git
- git clone https://github.com/Daniel-Elgarisi/ci-jenkins-infra.git

2. Start Jenkins
cd ci-jenkins-infra/jenkins
docker compose up -d

Jenkins available at:
http://localhost:8080

Create api token in jenkins
Configured in devportal-backstage/devportal-backstage/app-config.yaml:

jenkins:
- baseUrl: "http://localhost:8080"
- username: here you need to provide your own jenkins username
- apiKey: here you need to provide your own jenkins apiKey

3.Run backstage
cd devportal-backstage/devportal-backstage
export GITHUB_TOKEN= here you need to provide your own github token
yarn start

Access the UI:
http://localhost:3000

Backend:
http://localhost:7007

4.Configure kind
create 3 namespaces:
- dev
- staging
- prod

## Purpose

This Backstage instance provides a self-service CI/CD entry point.
Developers select deployment parameters and trigger an existing Jenkins pipeline.
The pipeline deploys to Kubernetes namespaces in an isolated and reproducible way.


## Infrastructure as Code

Jenkins jobs are provisioned automatically at container startup.
Groovy scripts under jenkins_home/init.groovy.d:

- create the pipeline-sample job

- configure parameters (ENVIRONMENT, IMAGE_TAG)

- link to the Jenkinsfile stored in the repository

No UI configuration is required.
This prevents drift and ensures reproducible CI/CD infrastructure.

### Deploy Sample Application — Flow Summary

1. Developer selects environment and image tag in Backstage.
2. Template triggers custom action `custom:jenkins:build`.
3. The action calls Jenkins REST API:
- POST /job/pipeline-sample/buildWithParameters
- ENVIRONMENT=<dev|staging|prod>
- IMAGE_TAG=<tag>
4. Jenkins pipeline deploys manifests using parameters.
5. Deployment is applied to the selected namespace.

## Key Components
### 1. Deployment Template

Location: catalog-templates/deploy-app/backstage-template.yaml

This template collects:

environment — dev or staging or prod

imageTag — Docker image tag to deploy

After submission the template calls a custom Jenkins action.

### 2. Custom Jenkins Action

Location: packages/backend/src/modules/customJenkinsBuild.ts

The action performs:

REST call to Jenkins using /buildWithParameters

Sends two parameters: ENVIRONMENT and IMAGE_TAG

Triggers the pipeline-sample Jenkins job

This approach was chosen because Jenkins plugins do not trigger parameterized jobs reliably;
therefore a dedicated REST action gives transparent control.

### 3. Jenkins Integration

Configured in app-config.yaml:

jenkins:
- baseUrl: "http://localhost:8080"
- username: here you need to provide your own jenkins username
- apiKey: here you need to provide your own jenkins apiKey

## How the Workflow Operates - Stage 2
Developer opens Create → Register existing component

Template prompts for:

URL: https://github.com/Daniel-Elgarisi/devportal-backstage/blob/main/catalog-info.yaml

Click on Analyze

Import

View component

Reload the page

click on Jenkins tab

## How the Workflow Operates - Stage 3

Developer opens Create → Deploy Sample Application

Template prompts for:

Environment

Image Tag

Template executes:

action: custom:jenkins:build

Backend action calls Jenkins REST API:

POST /job/pipeline-sample/buildWithParameters

Jenkins pipeline runs:

deploy to Kubernetes namespace

Application is deployed into:

dev or staging or prod

Namespaces are isolated so each environment is independent.

Related Repository

The Jenkins pipeline and Kubernetes manifests live separately here:

https://github.com/Daniel-Elgarisi/ci-jenkins-infra