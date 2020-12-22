<center><b><font size=6>微软Virtual Assistant</font></b></center>

Virtual Assistant的项目官方文档可见于https://microsoft.github.io/botframework-solutions/overview/virtual-assistant-solution/

[TOC]

# 项目架构

![image-20201221093756175](%E5%BE%AE%E8%BD%AFVA%E9%83%A8%E7%BD%B2%E6%96%B9%E6%B3%95_%E6%96%B0.assets/image-20201221093756175.png)

微软的Virtual Assistant本质是一个bot framework，这是一个聊天机器人框架，在global Azure上有专门的bot app service，在Azure China用通用的app service来承载bot framework。在聊天机器人框架下，可以填入多种功能，包括LUIS、QnA、Dispatch和Speech等

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

  https://github.com/tommyfan34/MS_VA_SDSDK/tree/main/SDSDK/Android-Sample-Release/sdsdk-android/Android-Sample-Release

  用于在安卓客户端测试Speech SDK的能力，包括STT、TTS和KWS。和官方版本相比有所修改

+ 