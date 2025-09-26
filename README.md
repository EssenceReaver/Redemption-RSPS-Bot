# Redemption RSPS Bot
This is a RSPS bot tool for the RSPS Redemption. It comes with plenty of hooks and a ton of functionality already built into the SDK. Feel free to clone the repository, build the tools yourself, and write your own scripts. I have included many scripts already, and if you want, you can get in touch with me and I will write new scripts for you based on your needs.

Join my Discord server if you want some assistance:

[![Discord](https://img.shields.io/discord/123456789012345678.svg?label=Discord&logo=discord&color=7289da)](https://discord.gg/csayJvptZ7)

## How to run
Inside the release section you will find a modded redemption.jar file, a RedemptionBotCore.jar file, and a zip file containing all of my pre-made scripts. You download these files and put them in the same directory (unzip the scripts folder). Alternatively you can build the files yourself. To run the client, you simply open the current directory in powershell / command prompt and use the following command:

- `java -jar .\ModdedRedemption.jar`

If you are sketched out from running some random jar file, you can get in touch with me via the discord invite above and I will send you instructions to modify your own client and you can read the next section on how to build the RedemptionBotCore.jar as well as the scripts

## Building the sources manually
To build the sources manually, you can clone the github repo into your desired location. Each folder represents an intellij project. I used JDK 24 for these projects.
1. Firstly you need to open RedemptionBotSdk in intelliJ or some IDE. RedemptionBotCore and Scripts rely on the SDK as an import in pom.xml:

        <dependencies>
            <dependency>
                <groupId>com.john</groupId>
                <artifactId>RedemptionBotSDK</artifactId>
                <version>1.0.0</version>
                <scope>compile</scope>
            </dependency>
        </dependencies>
   I did not upload the RedemptionBotSdk as a remote maven repository, so you need to build the repository within intelliJ or some IDE and run `mvn install` within a terminal window. This will add the repository locally and will allow RedemptionBotCore and Scripts to import it.

2. If you were able to successfully build and mvn install RedemptionBotSDK then next you open RedemptionBotCore as another intelliJ project. Clean, and package this project and you should have RedemptionBotCore1.0.0.jar as an output.
3. Open the ScriptWorkspace as another intelliJ project, and you can Build, clean, and package all my premade scripts as well.

## What does each script do?
You can view the ScriptInfo.txt file and find a small description of each script including where to start the script.

## Writing your own scripts
If you want to write your own scripts, I would suggest looking at the code within my premade scripts. There is a lot of functionality and you can analyze the RedemptionBotSdk to find other functions if they exist. By default you want to copy at least the headers of all of my scripts which handles AFK checks. You can get in touch with me via the discord invite above if you want some assistance.
