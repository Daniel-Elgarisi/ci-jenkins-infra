# CI Jenkins Infrastructure

This repository contains the local Jenkins infrastructure used for the DevOps Home Assignment.  
The goal is to provide a clean and reproducible CI environment that can be integrated with Backstage and extended with pipelines, seed jobs, or Groovy automation.

---

## Overview

The setup uses Docker Compose to run a standalone Jenkins controller.  
Persistent storage is mounted locally to ensure configuration, plugins, tokens, and job data remain intact across restarts.

This repository does not contain application source code or Backstage configuration.  
Its purpose is to serve as a dedicated CI infrastructure module.

---

## Start Jenkins

From the root of the repository:

```bash
docker compose up -d
```

Jenkins UI becomes available at:

```
http://localhost:8080
```

To stop the container:

```bash
docker compose down
```

To remove the container and keep data:

```bash
docker compose down
```

To remove everything including persistent data:

```bash
docker compose down -v
```

---

## Persistent Storage

Jenkins state is stored in:

```
./jenkins_home
```

It is mounted to the container at:

```
/var/jenkins_home
```

This directory holds:
- Jenkins system configuration
- Installed plugins
- Job definitions
- User metadata
- Logs

It must never be committed to Git.  
A `.gitignore` file is provided to enforce this.

---

## Initial Unlock

After first startup, Jenkins generates a setup password.  
Retrieve it using:

```bash
docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Paste the password in the Jenkins unlock UI.

Then:

1. Install recommended plugins
2. Create an admin user
3. Complete the setup onboarding

---

## Creating an API Token

Automation tools such as Backstage will use a Jenkins API token.

To create a token:

1. Open Jenkins UI
2. Go to:  
   **Manage Jenkins → Manage Users**
3. Select your user
4. Press **Configure**
5. Scroll to **API Tokens**
6. Create a new token
7. Store it securely

Do not store tokens in Git repositories.

---

## API Verification

You can verify Jenkins is reachable using its REST API.

Example:

```bash
curl -u "USER:TOKEN" http://localhost:8080/api/json
```

A valid response returns a JSON object describing the Jenkins instance, including:
- Jobs
- Views
- Build metadata

---

## Security Notes

- Do not commit `jenkins_home` contents
- Do not commit tokens, passwords, or user credentials
- Prefer scoped CI users rather than full administrators
- Use environment variables or secret stores for automation

---

## Scope

This Jenkins instance is intended for use with:
- Backstage CI integration
- Self-service deployment workflows
- Automated pipelines

No production hardening is applied at this stage.
