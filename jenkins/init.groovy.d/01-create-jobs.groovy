import jenkins.model.*
import hudson.model.*

println ">>> Initializing Jenkins Jobs..."

def instance = Jenkins.getInstance()

def jobName = "hello-backstage"

if (instance.getItem(jobName) == null) {
    def job = new FreeStyleProject(instance, jobName)
    job.buildersList.add(new hudson.tasks.Shell("echo Hello from Jenkins seed job"))
    instance.add(job, jobName)
    println ">>> Created job: ${jobName}"
} else {
    println ">>> Job already exists: ${jobName}"
}

instance.save()
