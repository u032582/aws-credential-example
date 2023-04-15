package com.example.demo;

import java.io.File;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Uploader {

	public static void main(String[] args) {

		// S3バケット名とリージョンを指定する
		String bucketName = "test0001aaa";
		Region region = Region.US_WEST_2;

		AwsCredentialsProviderChain cp = AwsCredentialsProviderChain.builder()
				// 優先度の高い認証情報プロバイダーを先に追加
				// ECSタスクロール
				.addCredentialsProvider(ContainerCredentialsProvider.builder().build())
				// ~/.aws/credentialsの 指定プロファイル
				.addCredentialsProvider(ProfileCredentialsProvider.create("mfa"))
				// JavaのSysemProperties、環境変数、~/.aws/credentialsのdefaultプロファイルなど
				.addCredentialsProvider(DefaultCredentialsProvider.create())
				.build();

		// S3クライアントを作成し、AWS認証情報を設定する
		S3Client s3 = S3Client.builder()
				.region(region)
				.credentialsProvider(cp)
				.build();

		// アップロードするファイルを指定する
		File file = new File("build.gradle");

		// アップロードするファイルに対応するS3オブジェクトを作成する
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(file.getName())
				.build();

		// アップロードするファイルの内容をリクエストボディに設定する
		RequestBody requestBody = RequestBody.fromFile(file);

		// ファイルをアップロードする
		PutObjectResponse res = s3.putObject(putObjectRequest, requestBody);

		System.out.println("Uploaded " + file.getName() + " to S3 bucket " + bucketName + ".");
	}
}