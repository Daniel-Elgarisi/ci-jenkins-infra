import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition

import hudson.plugins.git.*
import hudson.model.ParametersDefinitionProperty
import hudson.model.ChoiceParameterDefinition
import hudson.model.StringParameterDefinition

println ">>> Initializing Git-based Pipeline job..."

def instance = Jenkins.getInstance()
def jobName = "pipeline-sample"

def repoUrl = "https://github.com/Daniel-Elgarisi/ci-jenkins-infra.git"
def scriptPath = "pipelines/sample-pipeline/Jenkinsfile"
def credentialsId = "github-ci-token"


WorkflowJob job = instance.getItem(jobName) as WorkflowJob

if (job == null) {
    println ">>> Creating pipeline job: ${jobName}"

    job = new WorkflowJob(instance, jobName)
    instance.add(job, jobName)

    println ">>> Created Git pipeline job: ${jobName}"
} else {
    println ">>> Updating existing pipeline job: ${jobName}"
}

def scm = new GitSCM(
    [
        new UserRemoteConfig(
            repoUrl,
            null,
            null,
            credentialsId
        )
    ],
    [new BranchSpec("*/main")],
    false,
    Collections.emptyList(),
    null,
    null,
    Collections.emptyList()
)

def flowDefinition = new CpsScmFlowDefinition(scm, scriptPath)
job.setDefinition(flowDefinition)


def parameters = [
    new ChoiceParameterDefinition(
        "ENVIRONMENT",
        ["dev", "staging", "prod"] as String[],
        "Target environment to deploy to"
    ),
    new StringParameterDefinition(
        "IMAGE_TAG",
        "latest",
        "Docker image tag or application version"
    )
]

job.removeProperty(ParametersDefinitionProperty) 
job.addProperty(new ParametersDefinitionProperty(parameters))

job.save()
instance.save()

println ">>> Pipeline job configured with parameters ENVIRONMENT and IMAGE_TAG"
