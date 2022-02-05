## Project notes and what I learned at the end.

Lesson learned part of the project. I publish it on my blog slawomirstec.com and keep
it for documentation purposes. This is a record of what I learned during the implementation of this sample application.
It has no structure or 'flow', all my notes are here.

### Introduction and history.

I was experimenting with IaC on the AWS platform for about one year. I was using mostly the AWS CDK tool.
After some time, I switched to a serverless stack (SST) project that wraps around CDK and adds a layer that makes
serverless development a breeze.

I've implemented a few projects with backend and frontend in the javascript/typescript, most can be found on my
Github

https://github.com/stokilo

The biggest sample application, for now, is a pelefele.com website for real estate listings. The backend of this application
was implemented in TypeScript. Code was shared between frontend and backend (typescript models and enums).

The database was DynamoDB and API Gateway was configured to talk with lambdas that were the core of my REST API.
Everything was provisioned with SST which, with the live lambda reload feature, was a great fit for this stack.

This project copies the frontend part and subset of IaC from it. It replaces the backend with Java and Quarkus platform.

### Project stack

Backend is Java + Quarkus. Relational db (postgres) and the AWS RDS for hosting.

Frontend is still NuxtJS + Vue.

IaC is CDK wrapped around SST. Few lambdas are in typescript.

Some shell scripts for SSO, database and debugging port forwarding (remote development scenario).

### What was a purpose of replacing TypeScript backend with Java one?

The previous stack with Typescript and DynamoDB is perfect for quick prototyping, and I'm pretty sure it is the best
fit for many use cases. 

I had two main motivations here, replace Typescript backend with Java and DynamoDB with relation database.
My reasoning is below:

#### Database - DynamoDb design

My approach for DynamoDB storage was based on a single table design with storing complex objects in a single map column.
Such column stored serialized object as JSON. This is a bit different from what you see by default in tutorials, where
each object field is a column. I didn't follow this because this is too cumbersome and hard to maintain. For a small
project, I could not justify making it so complex. Serialized objects are too small to save any money on fetching them
from the store. I can afford to fetch the whole object and fit in the same RCU as I would by storing each field as a separate 
column.

I found that using ORM tools for DynamoDB is not a good decision. To talk with DynamoDB you must use the AWS API.
API requires many parameters that are most of the time repeated. I found it easier to implement own entities that
were sharing these parameters and explicitly define PK/SK format. Considering that all I needed for an entity is
PK/SK/column with serialized JSON, I was able to implement DAO layer in NoSQL that I quite liked. This kind of design
works ok for 1-1 or 1-MANY relations. Of course simple one, without complex filtering, ordering, etc.

Here it started to become a pain point in the sample application. Real estate listing requires
a lot of filtering which is not a good fit for the DynamoDB. Many DynamoDB experts will tell you that you should
use secondary indexes to handle all access patterns. This is fine but there are issues related to this approach. 
First, the cost of storing data and the problem with synchronization of writes.
This is of course not an issue with the real estate app. Such application will have very little write requests comparing to the
reads. But it is inconvenient even in such a case to deal with DynamoDB. You must be careful with designing access
patterns and monitor RCU/WCU all the time. I had the feeling that I waste time on tuning AWS service instead of developing 
my app.

I think that DynamoDB should be used for very simple object storage with basic filtering by 
natural order PK or creation date (timestamp). 

My solution for the filtering problem was adding an elastic search service. I've developed integration with AWS-managed instances.
It worked pretty well but this is a quite expensive service. Considering the utilization of this application I could not 
justify running it, managing, and paying for that. I don't think real production deployment would require it either.

I've decided to use a simple approach and move to RDS and do old good JPA with Postgres solution.

##### Typescript backend replacement

I wanted to switch from Typescript to Java to get back to my favorite programming language. I have nothing really against
Typescript. I think, that together with SST, it is the easiest way to work with lambda based REST Apis. 

Additionally, I wanted to refresh my Quarkus skills. I did some test deployments of the Quarkus on OpenShift and Rancher.
My idea was to dig deeper into Quarkus ecosystem and do deployment on AWS Fargate. This way I could focus on the business
side of the project and leave the whole container infrastructure for AWS.

#### Quarkus remote development

My initial idea was inspired by SST. I was thinking then about all the issues caused by mocking AWS services
on a local dev machine. I don't like to do it because it is always something missing on stage machines like wrong access
policies or misconfigured security groups.

My idea was like following. Run single container on Fargate and enable remote development on the Quarkus.
This allows editing code on a local machine and syncing with the app running on AWS. This is 1-1 setup as I would have
on PROD system with the exception that PROD would have remote mode disabled.

I did this and it worked very well. Better than expected I would say. I had to open debugging port on
Fargate container and application load balancer. Additionally, on my local Quarkus instance I disabled debugging.
For port forwarding, I was using bastion host. 

Some shell scripts like db.sh and debug.sh automate it. Can be found at the root of the project.

I did also integration with Fargate SPOT instances in the ECS stack. This allowed me to reserve a little better spec
for container. 

In the end, I've decided to not use this approach for development. I've decided to sacrifice the
flexibility of this setup and that it matches the production. This is because the Quarkus live reload will always take 
some time because of network latency and the time required to reload. The delay depends on container memory and the number
of CPU cores. It was quite ok for 2 CPU cores and 8GB of RAM, reloading times were taking a few seconds.
But some code changes could break the process and a restart was required. It doesn't take a lot of time to do it, but
I assumed that when the application becomes large, it will take much longer. 

I've decided to run the database locally and develop it with quarkus:dev plugin. All other AWS integrations are
done using my localhost credentials. This will sometimes require installing the app on the stage and fixing the AWS configurations.
In the final setup, I do local development against Postgres that runs from docker-compose script. Stage and Prod deployments
are done from the maven plugin that uploads data for ECR.

#### Project deployment pipeline

Once the image is deployed in the AWS ECR repository I have to restart service tasks manually. I've implemented a project
pipeline to restart it automatically once a new image is pushed to the ECR repo. This part of IaC is disabled because for simple
deployments I don't need it. For a larger project, I would enable this.

I do implement my IaC in Typescript. I've copied most of the code for Pipeline setup from AWS CDK documentation.
I don't really like CDK way of Pipeline construct. I would prefer to have a higher level construct to simply define
one of the predefined pipelines. Something like ecs_patterns for container services.
I don't find joy in defining building steps and testing if that works. I hope it will land soon in CDK.

I think I start to dislike IaC for AWS because of the time wasted trygin to deploy something via Cloudformation. When it works it is
perfect, and I love it to have. But it really takes a lot of time to set up some infrastructures and there is no benefit
of doing that for business. Anyway, I have working knowledge now on how to use it to set up apps from serverless to ec2 based
services. Thus, I like to see patterns implemented as a ready to use solutions for common infrastructure problems.

I would say, my perfect AWS CDK setup is like the following. You select part of your infrastructure you want to provision
from the CDK code. For example Fargate service behind a load balancer with WAF etc. Then based on this setup you can run
some post deployment actions that adjust it to your needs. However, the initial setup should be backed by AWS
and be deployed from the pools of warmed up services. Cloudformation is slow. I hate to wait for RDS 10 minutes
to ready to service requests. I would pay more to get it from the pool of pre-initialized services and only adjust to 
my needs (credentials, security groups, etc.). So kind of IaC for lazy devs :) Here on example of the pipeline I 
think I should get it ready by default and assigned to me when defined in the code. 
Th only thing I should configure is i.e. name of the ECR repo I want to pull and that is it. 

#### Quarkus AWS integration

######AWS Secret Manager and SSM  integration

Integration via SDK. Maven dependencies should be selected from Quarkus website from io.quarkiverse.amazonservices group.
This is required in order to have it working with a native image.

I'm using org.eclipse.microprofile.config.spi.ConfigSource to fetch the config and merge with microprofile one.

For database credentials integration I'm using io.quarkus.credentials.CredentialsProvider, this requires some
additional properties to register it during startup.

###### AWS Cognito integration

Cognito is provisioned and configured with values from the AWS Secret.

The Quarkus OIDC config requires defining an auth server URL. 

quarkus.oidc.auth-server-url

Let's say that the Cognito auth server url is: https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_jRMxU35MW

Append  /.well-known/openid-configuration to the url:

https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_jRMxU35MW/.well-known/openid-configuration

this should return auth server config. If that is the case then this URL can be used for config value.

I've additionally configured mapping from claim 'cognito:groups' to the app roles. For that, I've provisioned
two groups via Cloudformation:

    cognitoAdminGroupName: 'admin-group',
    cognitoRegularUserGroupName: 'regular-users-group',

By default post registration lambda function assign users to 'regular-users-group'.

To assign the user to the admin group you must use AWS Console.

These group assignments are checked on REST API endpoints level using @javax.annotation.security.RolesAllowed i.e.

@RolesAllowed({OIDCRoleType.OIDCRole.REGULAR_USER})

This works because the assigned claim is injected into the com.sstec.qpelefele.config.RolesAugmentor which
populate correct role in SecurityIdentity. 

In order to test these roles I'm using io.quarkus.test.security.TestSecurity annotation from test package that
supports mocking security. The Quarkus documentation has more details.

##### Database migration

Quarkus provides org.flywaydb.core.Flyway integration. I've integrated migration on startup and optional
programmatic migration. 

The database is migrated on application startup. What I didn't check is how to handle these migrations when
multiple containers are starting up. This is important to avoid db deadlocks for long-running scripts.

Make sure that you apply the correct value for:

quarkus.hibernate-orm.database.generation

I think the best is to enable the recreation of tables during development and integrate generated tables 
into the migration script. Non dev environments should rely on SQL migrations only.

######  Quarkus other comments

####### Typescript serialized

I generate client Typescript models from Java beans using maven plugin: cz.habarta.typescript-generator
All models annotated with @TypescriptSerializable are serialized and available in generated.ts file.
All clients components have access to these interfaces.

Classes are generated during generate-classes phase. Make sure you clean and rerun this goal after changes in 
the model to avoid a mismatch between client and server.

####### DTO and VM mapping

I've integrated MapStruct library to map DTO and VM beans to my entities and vice versa.
MapStruct has good support for Quarkus. No issue here, mapping is maintained here:

com.sstec.qpelefele.model.mapper.DTOConverter 

####### Postgres full text search

In order to get rid of complex and costly ES setup, I've decided to use Postgres full text search.
I needed it for the address autocomplete feature.

The database is populated with all addressed from the Polish registry (cities and streets only). 
These are inserted into LOCATION table and special column stores all lexemes. 

The query for autocomplete with mapping can be found here:

com.sstec.qpelefele.web.LocationSearchResource

Note, this native query that requires schema name injection.


#### Summary

I see a big boost in productivity using the classic RDS + Java backend stack. I prefer Typescript for lambdas.
That is why I've decided to keep SST for IaC. I can quickly develop and debug Typescript lambdas. These 
are used mostly for integration parts and callbacks. Quarkus supports lambda, but I would have to maintain 
a separate package for each of them and repeat deploy+test for every change. That is why I prefer the Typescript SST combo.

I predict that DynamoDB is a good fit for this stack. I will consider this to store session or cache data.
Because this is not critical usage I will be ok with mocking it with Quarkus provided DynamoDB client.

Native image build didn't work on my Apple Air M1 chip. Process stuck on hibernate package class shaving. 
I didn't have time to check why.

I was able to run this application on Fargate spec with 0.5GB RAM and 0.25 CPU . Startup time was around 3 seconds.
The application was responsive. This is cost of 10 $ per month for this single container.

Overall cost of this stack for single environment is 

ALB 20$
RDS 9$
Fargate 10$

let's say around 50$ per month. 







































