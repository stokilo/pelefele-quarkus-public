#!/bin/bash
# ssh port forwarding between AWS RDS and localhost

#set -o xtrace

lsof -ti tcp:5005 | xargs kill
echo "Killed pending SSH port forwarding (5005 JVM debug) sessions"

export BASTION_HOST_IP=$(aws ec2 describe-instances \
   --filters "Name=tag:Name,Values=BastionHost" "Name=instance-state-name,Values=running" \
   --output text \
   --query 'Reservations[0].Instances[0].PublicIpAddress')
echo "BASTION_HOST_IP $BASTION_HOST_IP"

export ECS_CLUSTER_ARN=$(aws ecs list-clusters --output text --query 'clusterArns[0]')
echo "ECS Cluster ARN: $ECS_CLUSTER_ARN"

export TASK_ARN=$(aws ecs list-tasks --cluster "$ECS_CLUSTER_ARN" --output text --query 'taskArns[0]')
echo "Task ARN: $TASK_ARN"

export TASK_ENI=$(aws ecs describe-tasks --cluster "$ECS_CLUSTER_ARN" --tasks "$TASK_ARN" \
  --output text --query 'tasks[0].attachments[0].details[1].value')
echo "Task ENI: $TASK_ENI"

export TASK_PUBLIC_IP=$(aws ec2 describe-network-interfaces --network-interface-ids "$TASK_ENI" --output text \
  --query 'NetworkInterfaces[0].Association.PublicIp')
echo "Task PublicIp: $TASK_PUBLIC_IP"

echo "--------------------------------------------------"
echo "------------- Running SSH Tunnel for 5005 --------"
echo "--------------------------------------------------"
ssh-add ~/.ssh/mumbai-ssh.pem
ssh -i "~/.ssh/mumbai-ssh.pem" -f -N -L 5005:$TASK_PUBLIC_IP:5005 ec2-user@$BASTION_HOST_IP -v
