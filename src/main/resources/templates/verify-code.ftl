<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>验证码</title>
    <style>
        :root {
            color-scheme: light dark;
        }

        body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
            background: #f6f7fb;
            color: #222;
        }

        .container {
            max-width: 560px;
            margin: 24px auto;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }

        .header {
            padding: 20px 24px;
            background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
            color: #fff;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .brand-logo {
            width: 32px;
            height: 32px;
            border-radius: 8px;
            background: rgba(255, 255, 255, 0.2);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
        }

        .brand-name {
            font-size: 16px;
            font-weight: 600;
            letter-spacing: 0.3px;
        }

        .content {
            padding: 24px;
        }

        .title {
            margin: 0 0 16px;
            font-size: 18px;
        }

        .desc {
            margin: 0 0 12px;
            line-height: 1.6;
            color: #555;
        }

        .code-box {
            margin: 20px 0 12px;
            padding: 16px 20px;
            background: #0ea5e90f;
            border: 1px dashed #38bdf8;
            border-radius: 10px;
            display: inline-flex;
            gap: 10px;
            align-items: center;
        }

        .code-label {
            color: #0ea5e9;
            font-weight: 600;
            letter-spacing: 1px;
        }

        .code-value {
            font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
            font-size: 22px;
            font-weight: 800;
            letter-spacing: 4px;
            color: #0ea5e9;
        }

        .meta {
            margin-top: 14px;
            font-size: 12px;
            color: #666;
        }

        .footer {
            padding: 16px 24px 22px;
            border-top: 1px solid #f0f2f5;
            font-size: 12px;
            color: #888;
        }

        .muted {
            color: #999;
        }

        @media (prefers-color-scheme: dark) {
            body {
                background: #0b0e13;
                color: #e5e7eb;
            }

            .container {
                background: #0f141b;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.6);
            }

            .footer {
                border-top-color: #1f2937;
                color: #9ca3af;
            }

            .desc {
                color: #c5cbd5;
            }

            .code-box {
                background: #0b1220;
                border-color: #114b7b;
            }

            .code-label, .code-value {
                color: #38bdf8;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="brand-logo">W</div>
        <div class="brand-name">${siteName!"WolfHouse Blog"}</div>
    </div>
    <div class="content">
        <h2 class="title">您好${email?has_content?then("，" + email, "")}</h2>
        <p class="desc">您正在 <a href="https://wolfblog.cn">Wolf Blog</a> 注册帐号。请在有效期内完成验证。</p>
        <div class="code-box">
            <span class="code-label">验证码</span>
            <span class="code-value">${code}</span>
        </div>
        <p class="desc">该验证码自发送起 <strong>${expireMinutes!30}</strong> 分钟内有效，请勿泄露给他人。</p>
        <div class="meta">
            发送时间：${sendTime!""}
        </div>
    </div>
    <div class="footer">
        如果这不是您的操作，请忽略此邮件。此邮件由系统自动发送，请勿直接回复。
    </div>
</div>
</body>
</html>
