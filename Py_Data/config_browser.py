from DrissionPage import ChromiumOptions

# 把引号里的路径，替换成你自己电脑上chrome.exe的路径
ChromiumOptions().set_browser_path(r'C:\Program Files\Google\Chrome\Application\chrome.exe').save()

print("Chrome浏览器配置完成")