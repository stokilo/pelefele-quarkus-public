#!/bin/bash
# ssh port forwarding between AWS RDS and localhost

#set -o xtrace

lsof -ti tcp:5432 | xargs kill
echo "Killed pending SSH port forwarding (5432 postgres) sessions"

export BASTION_HOST_IP=$(aws ec2 describe-instances \
   --filters "Name=tag:Name,Values=BastionHost" "Name=instance-state-name,Values=running" \
   --output text \
   --query 'Reservations[0].Instances[0].PublicIpAddress')

echo "--------------------------------------------------"
echo "-- Running SSH Tunnel for $BASTION_HOST_IP:5432 ---"
echo "--------------------------------------------------"
ssh-add ~/.ssh/mumbai-ssh.pem
ssh -i "~/.ssh/mumbai-ssh.pem" -f -N -L 5432:writer.rds.com:5432 ec2-user@$BASTION_HOST_IP -v
