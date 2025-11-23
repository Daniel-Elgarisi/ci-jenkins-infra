import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition

import hudson.plugins.git.*

println ">>> Initializing Git-based Pipeline job..."

def instance = Jenkins.getInstance()
def jobName = "pipeline-sample"

def repoUrl = "https://github.com/Daniel-Elgarisi/ci-jenkins-infra.git"
def scriptPath = "pipelines/sample-pipeline/Jenkinsfile"
def credentialsId = "github-ci-token"

if (instance.getItem(jobName) == null) {

    println ">>> Creating pipeline job: ${jobName}"

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

    def job = new WorkflowJob(instance, jobName)
    job.setDefinition(new CpsScmFlowDefinition(scm, scriptPath))

    instance.add(job, jobName)
    instance.save()

    println ">>> Created Git pipeline job: ${jobName}"
} else {
    println ">>> Pipeline job already exists: ${jobName}"
}
