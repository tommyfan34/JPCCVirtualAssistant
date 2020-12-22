<center><b><font size=6>微软Virtual Assistant</font></b></center>

Virtual Assistant的项目官方文档可见于https://microsoft.github.io/botframework-solutions/overview/virtual-assistant-solution/

[TOC]

# 项目架构

![image-20201221093756175](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221093756175.png)

微软的Virtual Assistant本质是一个bot framework，这是一个聊天机器人框架，在global Azure上有专门的bot app service，在Azure China用通用的app service来承载bot framework。在聊天机器人框架下，可以填入多种功能，包括LUIS、QnA、Dispatch和Speech等。目前Virtual Assistant的官方文档是基于global Azure的，并不适用于Azure中国（两个Azure是两套体系，账户互不通）。

## LUIS

portal页面：https://www.luis.ai/。LUIS是微软认知服务中的语义理解服务，负责对语句进行意图识别。对于每一个授权资源，可以部署根据语言、用途部署多个应用。比如在下面的资源中，根据中文、英文以及通用/分发分为了四个应用。

![image-20201221103354661](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221103354661.png)

### 意图(Intent)

每一个应用下都分为了多个意图(Intents)，意图即为LUIS判断出的用户所想要执行的动作。比如在Cancel意图中，有多个示例语句，比如no never mind、no no cancel等。LUIS根据这些输入示例可以训练一个模型，从而将相似的语句也能提取出同样的意图。

![image-20201221103802071](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221103802071.png)

![image-20201221103914401](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221103914401.png)

### 实体(Entity)

LUIS中的实体(Entity)概念是指一句话中有特殊意义的部分，该部分能够影响识别出意图之后VA执行的动作。比如请播放<font color="red">下一首</font>歌曲，这里的“下一首”就是一个实体，LUIS在提取到这个实体之后告知VA，VA就会执行播放清单中的下面第一首歌曲，而不是第二首或者其他的歌曲。下面是几种实体的示例。比如DirectionalReference是一种机器学习实体，表明可以给其提供一些示例样本，比如“右边”、“下面”等，LUIS可以延展到其他词语。number、ordinal等是prebuilt类型的实体，即微软已经帮助我们提前构建好了这个模型，不需要做其他动作。PersonName.Any是一种Pattern类型的实体，即当其符合正则表达式规定的某种样式时自动提取这个实体。比如([you can]|[please]) call me {PersonName.Any}[.]。

![image-20201221104239616](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221104239616.png)

在左边导航栏中的Review endpoint utterances中，可以将用户通过客户端发送的语句加入到模型的不同意图中，重新训练(Train)并发布(Publish)，以提高语义理解的准确性。

![image-20201221114343251](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221114343251.png)

![image-20201221114443505](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221114443505.png)

在Test中，可以直接对语句进行测试，如下面的OK识别的意图为Confirm，该意图的可信度为0.98

![image-20201221120252531](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221120252531.png)

## 问答(QnA Maker)

portal页面https://www.qnamaker.ai/

QnA Maker和LUIS一样，也是一种语言处理模型。QnA和LUIS的区别在于QnA无法对语言进行意图识别，只是单纯对某一句话进行精确匹配，并给出回答，适用于某些具有固定回答样式的对话内容。对于某一个授权资源，同样可以根据语言、功能有多个应用。如下所示，Faq、Chitchat分别代表频繁被提问的问题模型和日常聊天模型。

![image-20201221115828349](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221115828349.png)

在每个应用中，可以对对话进行编辑和训练。在Add alternative phrasing中，可以添加和该问题相同、但表现形式稍有区别的问题，比如“什么是虚拟助理”和“虚拟助理是啥”。在Add follow-up prompt中，可以在回答后增加提示语句。每个回答不一定是纯文本的形式，而可以加入图片、超链接等其他样式。

![image-20201221120521883](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221120521883.png)

## 分发器(Dispatcher)

参考文档：https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-tutorial-dispatch?view=azure-bot-service-4.0&tabs=cs

![Code sample logic flow cs](https://docs.microsoft.com/en-us/azure/bot-service/v4sdk/media/tutorial-dispatch/dispatch-logic-flow.png?view=azure-bot-service-4.0)

分发器是指将终结点输入的语句根据一定的规则分发给不同的语言处理模型，比如分给QnA或者LUIS的其他应用。当某句话在QnA模型中有相应的问题-回答对时，应该将这句话分发给QnA模型进行后续处理，否则分发给LUIS进行处理。分发器的本质是一个LUIS模型，当bot收到来自终结点的输入语句时，将会触发`OnMessageActivityAsync`回调，触发分发器模型，分发器会判断该语句的最高意图，并根据判断结果将该语句继续分流到QnA或者LUIS的其他应用(app)去。

## 语音服务

参考文档：https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/speech-sdk?tabs=android%2Cubuntu%2Cios-xcode%2Cmac-xcode%2Candroid-studio

微软语音服务(Speech Service)为虚拟助手提供了语音能力，使其变为“语音助手”。Speech Service主要的作用是进行语音转文本(STT)、文本转语音(TTS)和关键词发现(KWS)，其中STT和TTS都是需要云服务才能完成，KWS只需要在本地创建一个包含该关键词语音特征的模型，将输入的语音流和该模型匹配即可。创建该模型的方法可以参考https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/custom-keyword-basics?pivots=programming-language-csharp

STT和TTS都是在客户端调用SpeechSDK进行的。比如在SDSDK这个展示speechSDK能力的android app里，STT的代码为

```java
// SDSDK\SDSDK\Android-Sample-Release\sdsdk-android\Android-Sample-Release\example\app\src\main\java\com\microsoft\cognitiveservices\speech\samples\sdsdkstarterapp\MainActivity.java
final SpeechRecognizer reco = new SpeechRecognizer(this.getSpeechConfig(), this.getAudioConfig());
reco.recognizing.addEventListener((o, speechRecognitionResultEventArgs) -> {
	final String s = speechRecognitionResultEventArgs.getResult().getText();
	setRecognizedText(s, true);
});
final Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
setOnTaskCompletedListener(task, result -> {
    final String s = result.getText();
    reco.close();
    setRecognizedText(s, true);
});
```

在Virtual Assistant的demo android app里，STT的代码为

```java
// MS_VA_AndroidClient\directlinespeech\src\main\java\com\microsoft\bot\builder\solutions\directlinespeech\SpeechSdk.java
public void listenOnceAsync(){
    EventBus.getDefault().post(new BotListening());
    final Future<SpeechRecognitionResult> task = botConnector.listenOnceAsync();
    setOnTaskCompletedListener(task, result -> {
        // your code here
    });
}

// com\microsoft\cognitiveservices\speech\dialog\DiaglogServiceConnector.class
public Future<SpeechRecognitionResult> listenOnceAsync() {
    return this.executorService.submit(new Callable<SpeechRecognitionResult>() {
        public SpeechRecognitionResult call() {
            IntRef var1 = new IntRef(0L);
                Contracts.throwIfFail(DialogServiceConnector.this.listenOnce(DialogServiceConnector.this.dialogServiceConnectorHandle, var1));
            return DialogServiceConnector.this.new DialogSpeechRecognitionResult(var1.getValue());
        }
    });
}
```

可以看到，在VA的代码中，`listenOnceAsync`中任务完成的回调函数没有任何动作，这是因为VA默认用户发出的语音在到达speech service之后直接通过directline speech这个通道发送给了Bot service，由Bot service来处理STT之后的结果，而在SDSDK中由于没有连接directline speech，因此直接将STT的结果展示在了app界面。

SDSDK的TTS代码为

```java
String s = "";
s = ttsInput.getText().toString();
synthesizer.SpeakText(s);
```

通过`synthesizer.SpeakText()`来将文本转为语音

VA Client中没有TTS的相关代码，取而代之的是直接从云端获取audiostream进行播放。由此也可以知道，当VA连接了directline speech之后，云端直接将文字转换为了语音流进行返回。

```java
botConnector.activityReceived.addEventListener((o, activityEventArgs) -> {
    final String json = activityEventArgs.getActivity();
    logLongInfoMessage(LOGTAG, "received activity: " + json);

    if (activityEventArgs.hasAudio()) {
        // cancel response timeout timer
        // note: located here because a lot of activity events are received,
        //       by putting it here, only one event (with speech) cancels the timer.
        cancelResponseTimeoutTimer();

        LogInfo("Activity Has Audio");
        PullAudioOutputStream outputStream = activityEventArgs.getAudio();
        synthesizer.playStream(outputStream);
    }

    activityReceived(json);
});
```

## Channel

bot framework支持从包括Teams、Slack等多种客户端访问，访问的信道叫做channel。在本项目中，使用Direct Line Speech或者Direct Line。Direct Line Speech工作流程如下所示

![Conceptual diagram of the Direct Line Speech orchestration service flow](https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/media/voice-assistants/overview-directlinespeech.png)

Direct Line则是客户端和云端bot之间访问的标准信道，不支持传输语音流。

## 技能(Skill)

参考文档：https://docs.microsoft.com/en-us/azure/bot-service/skills-conceptual?view=azure-bot-service-4.0

Skill也是一种Bot，只不过它用于完成某一种特殊功能，比如天气、POI功能等，不直接和用户进行交互。直接和用户进行交互的Bot称为root bot。skill的作用是可以提供功能复用，即不同的root bot可以同时连接相同功能的skill，从而减少在root bot中实现类似功能的冗余代码。

![Block diagram](https://docs.microsoft.com/en-us/azure/bot-service/v4sdk/media/skills-block-diagram.png?view=azure-bot-service-4.0)

# 软件需求

+ .NET Core SDK v3.1

  https://download.visualstudio.microsoft.com/download/pr/3366b2e6-ed46-48ae-bf7b-f5804f6ee4c9/186f681ff967b509c6c9ad31d3d343da/dotnet-sdk-3.1.404-win-x64.exe

  这是使用C#构建bot时在本地测试必需的SDK

+ npm 14.15.3 LTS 或更高

  https://nodejs.org/dist/v14.15.3/node-v14.15.3-x64.msi

  包管理工具，用于安装Bot Framework CLI等

  **注意**：在安装完npm之后需要添加国内镜像源，否则npm可能会出现`rollbackFailedOptional`报错

  ```powershell
  npm config set registry http://registry.npm.taobao.org
  ```

+ Powershell 7

  https://github.com/PowerShell/PowerShell/releases/download/v7.1.0/PowerShell-7.1.0-win-x64.msi

  命令行shell

+ Bot Framework CLI (Command-Line-Interface) Tool

  ```powershell
  npm install -g botdispatch @microsoft/botframework-cli
  ```

  Bot Skill CLI Tool

  ```powershell
  npm install -g botskills@latest
  ```

+ Azure CLI

  https://aka.ms/installazurecliwindows

  能够在命令行中管理Azure

+ Virtual Assistant 云端源码

  https://github.com/microsoft/botframework-solutions/tree/master/samples/csharp/assistants/virtual-assistant

  实现VA的C#代码，基于.NET v3.1运行，将从本地端部署到云端。

+ VA Android Client

  https://github.com/tommyfan34/MS_VA_AndroidClient

  Virtual Assistant的安卓客户端，安装在车机上。和官方版本相比有所修改，修复了几个bug

+ Bot Framework Emulator [非必要]

  https://github.com/microsoft/BotFramework-Emulator/releases/download/v4.11.0/BotFramework-Emulator-4.11.0-windows-setup.exe

  用于在本地测试VA，而不需要将VA部署到云端即可与VA进行交互。

+ SDSDK [非必要]

  https://github.com/tommyfan34/JPCCVirtualAssistant/tree/main/SDSDK/Android-Sample-Release/sdsdk-android

  用于在安卓客户端测试Speech SDK的能力，包括STT、TTS和KWS。和官方版本相比有所修改

+ VA Windows Client [非必要]

  https://github.com/tommyfan34/JPCCVirtualAssistant/tree/main/WindowsVoiceAssistantClient-20201120.6/WindowsVoiceAssistantClient-20201120.6

  用于在Windows测试部署在云端的VA

# C#部署VA方法

## 纯文本VA

开发环境为Windows，源码语言为C#，注意官方文档中的typescript会存在问题.

参考教程：https://microsoft.github.io/botframework-solutions/virtual-assistant/tutorials/create-assistant/csharp/1-intro/

从https://github.com/microsoft/botframework-solutions/tree/master/samples/csharp/assistants/virtual-assistant/VirtualAssistantSample下载源码。

### 部署到global Azure

在deployment\scripts\deploy_cognitive_models.ps1和deployment\scripts\deploy.ps1中，分别设置

```powershell
[string] $languages = "en-us,zh-cn",
```

以使其同时部署两种语言的LUIS model

运行脚本以部署其到Azure上

在VirtualAssistantSample文件夹下

```powershell
./Deployment/Scripts/deploy.ps1
```

自定义bot name，选择部署区域为westus，记录Microsoft App密码，选择创建新的LUIS授权资源，LUIS资源区域为westus

部署之后，在Azure portal相应的资源组中可以看到以下部署的资源

![image-20201222160729335](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201222160729335.png)

前三个是和LUIS相关的资源，Web App Bot是一个专为Bot Framework定制的web app，用于承载bot framework的源代码，是整个VA的核心资源。Application Insights是用于对用户行为进行分析的资源。App Service plan用于规定定价层等，App Service规定了终结点等配置。JOYNEXT-qna-p2tscpo是QnA的服务，joynext-search-p2tscpo用来对QnA的问答对的索引进行搜索。Azure Cosmos DB用来存储bot的状态，必不可少。storage account用来存储bot transcript，即对话历史，是可选项。

### 本地测试

参考：https://docs.microsoft.com/en-us/azure/bot-service/bot-service-debug-emulator?view=azure-bot-service-4.0&tabs=csharp

操作前注意：代理需要全部关闭

![image-20201130092745879](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201130092745879.png)

在项目文件夹下run

```powershell
dotnet run
```

正常情况下会出现

```powershell
info: Microsoft.Hosting.Lifetime[0]
      Now listening on: http://localhost:5000
info: Microsoft.Hosting.Lifetime[0]
      Now listening on: https://localhost:5001
info: Microsoft.Hosting.Lifetime[0] 
```

复制localhost的端口号，在emulator下的Endpoint URL中填入http://localhost:portnumber/api/messages，如http://localhost:5000/api/messages.

![image-20201130094319608](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201130094319608.png)

在Microsoft App ID和Microsoft App Password中填入appsetting.json中的microsoftAppId和microsoftAppPassword

### 将更新后的bot发布到Azure

参考文档：https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-deploy-az-cli?view=azure-bot-service-4.0&tabs=csharp

如果我们在本地修改了bot的代码，希望重新部署到云端，则进行如下步骤：

1. 登录Azure

   ```powershell
   az login
   ```

2. 生成.deployment文件

   ```powershell
   az bot prepare-deploy --lang Csharp --code-dir "." --proj-file-path "MyBot.csproj"
   ```

   这里的Mybot.csproj改为实际的.csproj文件名称

3. 将整个项目文件夹压缩为zip

4. 部署到云端

   ```powershell
   az webapp deployment source config-zip --resource-group "<resource-group-name>" --name "<name-of-web-app>" --src "<project-zip-path>"
   ```

   这里的<resource-group-name>、<name-of-web-app>、<project-zip-path>都是实际的资源组名称、web app名称和zip路径。

## 将speech连接到VA

前面的方法都是纯文本交互的VA，而没有用到语音能力。以下步骤将微软的语音服务能力(STT、TTS)连接到VA。

参考文档：https://docs.microsoft.com/en-us/azure/bot-service/bot-service-channel-connect-directlinespeech?view=azure-bot-service-4.0

在Azure中创建一个speech service

![image-20201130115313606](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201130115313606.png)

在web bot service中的channel添加direct line speech

![image-20201130115508661](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201130115508661.png)

在资源组中的app service资源，找到配置-常规设置-Web套接字-开-保存

![image-20201130115828783](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201130115828783.png)

## 将skill连接到VA

参考：[Intro (microsoft.github.io)](https://microsoft.github.io/botframework-solutions/skills/tutorials/add-prebuilt-skill/1-intro/)

skill实际上是另外一个bot，需要在VA上补充一个dispatcher来将utterance分流到一个skill intent，再来invoke这个remote skill

Calendar 等skill的源代码：https://github.com/microsoft/botframework-components

**注意**：源码中Calendar skill的LUIS模型有一个小问题，在Deployment/Resources/LU/zh-cn/Calendar.lu的第1627行上，将

```
- (查找|预订)[(会议室)|(房间)]{MeetingRoomPatternAny}
```

改为

```
- (查找|预订)[(会议室|房间)]{MeetingRoomPatternAny}
```

在VA源代码文件夹下

```powershell
npm install -g botdispatch @microsoft/botframework-cli
npm install -g botskills@latest
az login
```

修改deploy.ps1和deploy_cognitive_models.ps1的$languages="en-us,zh-cn"

```powershell
./Deployment/Scripts/deploy.ps1
```

更新skill manifest

将wwwroot/manifest/manifest-1.1.json下的{YOUR_SKILL_URL}替换为skill bot的终结点，形如https://joynext-cafskill-op2rcuz.azurewebsites.net/api/messages

将{YOUR_SKILL_APP_ID}替换为skill bot的app ID，形如b9735082-3185-46f3-9e55-93e7fd86f07d

添加依赖项，在CalendarSkill.csproj的Package Reference下，替换

```csharp
<PackageReference Include="Google.Apis.Calendar.v3" Version="1.40.2.1620" />
    <PackageReference Include="Google.Apis.People.v1" Version="1.25.0.830" />
    <PackageReference Include="HtmlAgilityPack" Version="1.11.7" />
    <PackageReference Include="Microsoft.AspNetCore.Mvc.NewtonsoftJson" Version="3.1.0" />    
    <PackageReference Include="Microsoft.Azure.CognitiveServices.Language" Version="1.0.1-preview" />
    <PackageReference Include="Microsoft.Azure.CognitiveServices.Search.NewsSearch" Version="2.0.0" />
    <PackageReference Include="Microsoft.Azure.Search" Version="10.0.1" />
    <PackageReference Include="Microsoft.Bot.Builder.AI.Luis" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Builder.AI.QnA" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Builder.ApplicationInsights" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Builder.Azure" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Builder.Integration.ApplicationInsights.Core" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Builder.Integration.AspNet.Core" Version="4.9.3" />
    <PackageReference Include="Microsoft.Bot.Solutions" Version="1.0.1" />
    <PackageReference Include="Microsoft.Graph" Version="3.7.0" />
    <PackageReference Include="Microsoft.Extensions.Configuration" Version="5.0.0" />
```

更新部署

```powershell
.\Deployment\Scripts\publish.ps1 -botWebAppName {YOUR_SKILL_BOTWEBAPP_NAME} -resourceGroup {YOUR_RESOURCEGROUP_NAME}
```

<font color="red">注意：之后可能还有bug，需要解决</font>

## 在Azure China构建VA

https://github.com/microsoft/botframework-solutions/tree/master/samples/csharp/assistants/virtual-assistant/VirtualAssistantSample中提供的VirtualAssistantSample仅仅适用于global Azure，不能直接使用这个例子来构建VA到Azure China。主要的问题在于：global Azure提供了web app bot这个专门为bot framework构建的web app，而Azure China尚未支持web app bot，这就会导致直接使用上述sample code中的deploy.ps1的脚本来部署会发生错误。为了解决这个问题，我们采用了一个迂回的解决方案，即直接在Azure China部署web app，再把bot framework代码部署到这个web app上。也就是说，VA是一个在bot framework的基础上填充了LUIS、QnA等能力的现成的解决方案，只不过这个解决方案只能在global Azure上使用。因此我们要做的是从头在web app上搭建bot framework这个骨架，再把LUIS、QnA、Speech等填充到骨架里。

注意，以下操作需要让Azure CLI地域切换到中国

```powershell
az cloud set -n AzureChinaCloud
```

### 本地构建基本机器人

参考文档：https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-tutorial-create-basic-bot?view=azure-bot-service-4.0&tabs=csharp%2Cvc

先在本地构建一个机器人服务，这里用echo bot作为演示。下载https://github.com/microsoft/BotBuilder-Samples/tree/main/samples/csharp_dotnetcore/02.echo-bot

在项目文件夹下，使用`dotnet run`测试，在浏览器中打开http://localhost:5000，测试是否能够正常运行

### 将机器人部署到web app

参考文档：https://docs.azure.cn/zh-cn/app-service/quickstart-dotnetcore?pivots=platform-linux

初始化git存储库

```powershell
git init
git add .
git commit -m "first commit"
```

创建部署用户

```powershell
az webapp deployment user set --user-name <username> --password <password>
```

username: joynext      password: Joynext!!

创建资源组

```powershell
az group create --name myResourceGroup --location "China East 2"
```

创建Azure应用服务计划

```powershell
az appservice plan create --name myAppServicePlan --resource-group myResourceGroup --sku F1 --is-linux
```

创建web app

```powershell
az --% webapp create --resource-group myResourceGroup --plan myAppServicePlan --name <app-name> --runtime "DOTNETCORE|3.1" --deployment-local-git
```

记录`deploymentLocalGitUrl`中的参数

https://joynext@joynext-cafvatest-bot.scm.chinacloudsites.cn/JOYNEXT-CAFVATEST-bot.git

测试一下是否部署成功

浏览器中打开https://<app-name>.chinacloudsites.cn

![image-20201214093740071](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201214093740071.png)

从git推送到Azure

```powershell
git remote add azure <deploymentLocalGitUrl-from-create-step>
git push azure master
```

使用bot framework emulator进行测试

https://github.com/Microsoft/BotFramework-Emulator/wiki/Tunneling-(ngrok)下载ngrok，环境变量添加ngrok.exe的path，并在emulator的设置中添加ngrok.exe的路径，确认tunnel status是active的

![image-20201214114913537](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201214114913537.png)

open bot，Bot URL为http://[appName].chinacloudsites.cn/api/messages

![image-20201214114831296](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201214114831296.png)

测试成功

![image-20201214115043042](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201214115043042.png)

<font color="red">以上操作仅仅是将一个echo bot部署到了web app，还需要将LUIS、QnA乃至speech连接到这个web app。LUIS和QnA连接到web app可以参考https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-tutorial-add-qna?view=azure-bot-service-4.0&tabs=csharp和https://github.com/microsoft/BotBuilder-Samples/tree/main/samples/csharp_dotnetcore/13.core-bot等其他方案。speech连接到web app比较复杂。由于VA的解决方案中，默认是通过direct line speech这个通道直接将客户端的语音流接入到speech service，在云端进行STT、TTS，客户端实际上不能获取STT的结果。而由于direct line speech是只在global Azure提供的，因此在Azure China，需要将direct line speech替换为direct line，具体操作是：先在客户端调用speechSDK中的STT（这一部分可以参考SDSDK的相关代码，前面已经提到过），从云端获得了文字之后，将文字再通过direct line发送给承载了bot framework的web app，web app再调用LUIS等语言理解模型，并返回相应的文字结果，然后客户端再调用speechSDK中的TTS来获得语音（同样可以参考SDSDK中的相关代码）</font>

# VA安卓客户端

VA安卓客户端的代码位于：https://github.com/tommyfan34/JPCCVirtualAssistant/tree/main/MS_VA_AndroidClient

应该可以直接编译运行。这个VA安卓客户端只适用于global Azure，因为它是通过directline speech和bot进行交互的，如果要使用direct line进行交互，需要修改代码。

注意：在https://github.com/tommyfan34/JPCCVirtualAssistant/blob/main/MS_VA_AndroidClient/directlinespeech/src/main/assets/default_configuration.json这个文件，修改SpeechSubscriptionKey和SpeechRegion分别为Azure中订阅的speech service的订阅密钥和订阅区域

# 一些常用命令

## Azure CLI地域切换

将Azure CLI地区切换到中国

```powershell
az cloud set -n AzureChinaCloud
```

将Azure CLI地区切换到全球

```powershell
az cloud set -n AzureCloud
```

登录Azure China

```powershell
az login
```

## CAF及adb常用操作

### CAF开启debug

为了实现CAF调试，需要连接串口，打开CAF调试功能。在putty上选择serial连接，端口为COM4（根据不同电脑可能不同），波特率为115200. 在putty命令行中键入

```shell
echo "peripheral" >/sys/devices/platform/soc/ee080200.usb-phy/role
```

### adb常用操作

安装apk

```shell
adb install [packagename]
```

查看已经安装的apk

```shell
adb shell pm list packages
```

查看某个包的权限信息

```shell
adb shell dumpsys package [packagename]
```

强制卸载package

```powershell
adb shell pm uninstall -k --user 0 [packagename]
```

### 麦克风占用问题

CAF中由于叮当语音和wecarcontrol的占用麦克风的关系，其他的应用无法使用麦克风，可能会造成

```
E/AudioRecord: start() status -38
```

报错的问题，解决方法：

尝试在代码中加入`validateMicAvailablity()`验证麦克风是否被占用

```java
private boolean validateMicAvailability(){
    Boolean available = true;
    AudioRecord recorder =
            new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_DEFAULT, 44100);
    try{
        if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED ){
            available = false;
        }

        recorder.startRecording();
        if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING){
            recorder.stop();
            available = false;
        }
        recorder.stop();
    } finally{
        recorder.release();
        recorder = null;
    }
    return available;
}
```

可尝试卸载叮当语音和wecarcontrol，再进行尝试。

由于CAF系统是Android9，因此优先级抢占的方法无法使用（需要Android10及以上版本）

