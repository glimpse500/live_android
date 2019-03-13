二开注意事项

1.应该用方维公司的源码当作主干，然后从主干建立分支，在分支上面进行二开。这样子后续如果有新的源码的话，可以把新的源码覆盖到主干，然后再同步到分支

2.直播间Activity结构如下

主播和观众的基类：
LiveActivity（直播间基类）
LiveLayoutGameActivity（游戏基类）
LiveLayoutGameExtendActivity（游戏界面扩展）
LiveLayoutActivity（公共界面）
LiveLayoutExtendActivity（公共界面扩展）


主播的继承类
LiveLayoutCreaterActivity（主播）                               			
LiveLayoutCreaterExtendActivity（主播扩展）                     			
LiveCreaterActivity（互动直播sdk主播）LivePushCreaterActivity（直播sdk主播）


观众的继承类
LiveLayoutViewerActivity（观众）
LiveLayoutViewerExtendActivity（观众扩展）
LiveViewerActivity（互动直播sdk观众）LivePushViewerActivity（直播sdk观众）LivePlayActivity（直播sdk播放基类）
																					|
																		  LivePlaybackActivity（回放）