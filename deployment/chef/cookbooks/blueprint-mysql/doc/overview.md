This is the wrapper cookbook to install mysql.

If you want to use .kitchen.ec2.yml to test this cookbook on amazon linux, please provide these environment variables:

```
export AWS_SSH_KEY_ID="your-key"
export AWS_AVALIBILITY_ZONE='eu-west-1c'
export AWS_REGION='eu-west-1'
export AWS_SUBNET_ID='subnet-xxxxxxx'
export AWS_SECURITY_GROUP_ID='sg-xxxxxx'
export AWS_VPC_ID='vpc-xxxxxxxxx'
export AWS_SHARED_CREDENTIALS_PROFILE='your_aws_profile'
```

