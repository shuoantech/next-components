<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>借款成本分析报告</title>
    <style>
        /* ============================================================
               PDF 全局设置
               ============================================================ */
        @page {
            size: A4 portrait;
            margin: 0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: "PingFang SC", "Microsoft YaHei", "SimSun", "宋体", serif;
            background: #f2f4f8;
            padding: 20px;
            font-size: 12px;
            color: #1e2a3a;
            line-height: 1.7;
            display: flex;
            justify-content: center;
        }

        .page {
            width: 210mm;
            background: #ffffff;
            padding: 0;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            border-radius: 4px;
            overflow: visible;
            position: relative;
        }

        /* ============================================================
               封面样式
               ============================================================ */
        .cover-wrapper {
            height: 297mm;
            width: 100%;
            page-break-after: always;
            break-after: page;
            overflow: hidden;
            margin: 0;
            padding: 0;
        }

        .cover-page {
            height: 297mm;
            width: 100%;
            display: flex;
            flex-direction: column;
            justify-content: flex-end;
            align-items: flex-start;
            text-align: left;
            padding: 50px 40px;
            background: url(https://lift-cloud.tos-cn-shanghai.volces.com/img/fengmian2.png) no-repeat center;
            background-size: cover;
            position: relative;
            overflow: hidden;
        }

        .cover-page::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 6px;
            background: linear-gradient(90deg, #3b92ed, #6ab0ff, #3b92ed);
            z-index: 1;
        }

        .cover-page .cover-title-wrapper {
            position: relative;
            z-index: 2;
        }

        .cover-page .report-title {
            font-size: 34px;
            font-weight: 700;
            color: #0a2540;
            letter-spacing: 8px;
            padding-bottom: 12px;
            border-bottom: 3px solid #3b92ed;
            display: inline-block;
            position: relative;
            z-index: 2;
        }

        .cover-page .report-sub-title {
            font-size: 32px;
            color: #000000;
            margin-top: 14px;
            letter-spacing: 4px;
            font-weight: 300;
            position: relative;
            z-index: 2;
        }

        .cover-page .report-date {
            font-size: 18px;
            color: #1e2a3a;
            margin-top: 30px;
            padding: 8px 24px;
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(4px);
            border-radius: 20px;
            letter-spacing: 2px;
            border: 1px solid rgba(59, 146, 237, 0.2);
            display: inline-block;
            position: relative;
            z-index: 2;
        }

        /* ============================================================
               内容区域 - 优化间距
               ============================================================ */
        .page-inner {
            padding: 6mm 12mm 4mm 12mm;
            page-break-before: auto;
            margin-top: 0;
        }

        .section-title {
            font-size: 20px;
            font-weight: 600;
            color: #3b92ed;
            padding: 6px 0 4px 0;
            border-bottom: 2px solid #3b92ed;
            margin: 10px 0 8px 0;
            display: block;
            letter-spacing: 1px;
            page-break-after: avoid;
        }

        .section-title-sm {
            font-size: 16px;
            font-weight: 600;
            color: #0a2540;
            margin: 6px 0 4px 0;
            display: block;
            padding-left: 12px;
            border-left: 4px solid #3b92ed;
            page-break-after: avoid;
        }

        .summary-text {
            font-size: 14px;
            color: #1e2a3a;
            line-height: 1.8;
            text-align: justify;
            background: #f8faff;
            border-radius: 6px;
            padding: 10px 14px;
            page-break-inside: avoid;
            margin-bottom: 4px;
        }

        .summary-text .highlight-red {
            color: #c0392b;
            font-weight: 700;
            font-size: 16px;
        }

        .reg-link {
            font-size: 15px;
            font-weight: 600;
            color: #c0392b;
            display: inline-block;
            margin: 6px 0 4px 0;
            padding: 0 4px;
        }

        .img-wrapper {
            page-break-inside: avoid;
            page-break-after: avoid;
            page-break-before: auto;
            margin: 4px 0;
        }

        .img-responsive,
        .img-regulations {
            display: block;
            max-width: 100%;
            width: 100%;
            height: auto;
            margin: 0 auto;
            border-radius: 4px;
            page-break-inside: avoid;
            page-break-after: avoid;
        }

        .cost-table {
            width: 100%;
            border-collapse: collapse;
            margin: 4px 0 4px 0;
            font-size: 14px;
            border-radius: 6px;
            overflow: hidden;
            page-break-inside: avoid;
        }

        .cost-table td {
            padding: 6px 18px;
            border-bottom: 1px solid #eef2f7;
        }

        .cost-table tr:last-child td {
            border-bottom: none;
        }

        .cost-table .label-col {
            background: #f5f8fd;
            color: #4a5a6e;
            font-weight: 500;
            width: 38%;
        }

        .cost-table .value-col {
            color: #0a2540;
            font-weight: 600;
            text-align: center;
            width: 62%;
            font-size: 15px;
        }

        .total-amount-box {
            font-size: 16px;
            font-weight: 700;
            color: #b8860b;
            text-align: center;
            padding: 8px 16px;
            background: #fdf8ee;
            border-radius: 6px;
            border: 1px solid #f0dca0;
            margin: 4px 0 6px 0;
            letter-spacing: 2px;
            page-break-inside: avoid;
        }
        .img-chengben {
            display: block;
            max-width: 100%;
            width: 100%;
            height: auto;
            margin: 0 auto;
            border-radius: 5px;
            page-break-inside: avoid;
            page-break-after: avoid;
        }
        .irr-box {
            background: #f5f8fd;
            padding: 10px 16px;
            border-left: 5px solid #3b92ed;
            border-radius: 0 6px 6px 0;
            margin: 4px 0 4px 0;
            font-size: 13px;
            line-height: 1.9;
            color: #3a4a5e;
            page-break-inside: avoid;
        }

        .irr-box .irr-label {
            font-weight: 700;
            color: #0a2540;
            font-size: 15px;
            display: block;
            margin-bottom: 2px;
        }

        .irr-result {
            font-size: 15px;
            color: #1e2a3a;
            text-align: left;
            background: #fafffe;
            border-radius: 6px;
            padding: 8px 16px;
            border: 1px solid #e8edf5;
            page-break-inside: avoid;
            margin-bottom: 4px;
        }

        .irr-result .highlight-blue {
            color: #2980b9;
            font-weight: 700;
        }

        .irr-result .highlight-red {
            color: #c0392b;
            font-weight: 700;
            font-size: 17px;
        }

         .image_4 {
            width: 21px;
            height: 19px;
            margin-top: 1px;
        }

        /* ============================================================
               还款计划表格
               ============================================================ */
        .repayment-table-wrap {
            width: 100%;
            margin: 4px 0 12px 0;
            border-radius: 6px;
            overflow: hidden;
            border: 1px solid #dce3ed;
            page-break-inside: avoid;
            page-break-after: auto;
        }

        .repayment-table-wrap table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            font-size: 11.5px;
        }

        .repayment-table-wrap th,
        .repayment-table-wrap td {
            padding: 4px 4px;
            text-align: center;
            vertical-align: middle;
            border: 1px solid #dce3ed;
        }

        .repayment-table-wrap thead th {
            background-color: #2c6fbb;
            color: #ffffff;
            font-size: 12px;
            font-weight: 600;
            height: 28px;
            letter-spacing: 0.5px;
        }

        .repayment-table-wrap tbody tr {
            background-color: #ffffff;
        }

        .repayment-table-wrap tbody tr.alt {
            background-color: #f4f8ff;
        }

        .repayment-table-wrap tbody td {
            color: #1e2a3a;
            height: 22px;
            font-size: 11.5px;
        }

        .repayment-table-wrap .col-period { width: 16%; }
        .repayment-table-wrap .col-actual { width: 28%; }
        .repayment-table-wrap .col-legal { width: 31%; }
        .repayment-table-wrap .col-excess { width: 25%; }

        .repayment-table-wrap .blue-bg {
            background-color: #2c6fbb;
            color: #ffffff !important;
            font-weight: 600;
        }

        .repayment-table-wrap tfoot td {
            background-color: #fdf8ee;
            font-weight: 700;
            color: #b8860b !important;
            height: 26px;
            border-top: 2px solid #e8c86a;
            font-size: 12.5px;
        }

        .repayment-table-wrap tfoot .blue-bg {
            background-color: #1e5a9e;
            color: #ffffff !important;
        }

        /* 描述文字 */
        .desc-text {
            font-size: 13.5px;
            color: #1e2a3a;
            margin: 2px 0 6px 0;
            background: #f8faff;
            padding: 6px 14px;
            border-radius: 6px;
            page-break-inside: avoid;
        }

        /* ============================================================
               底部图片 - 优化间距
               ============================================================ */
        .img-wrapper {
            margin-top: 10px;  /* 从6px增加到10px，增加上方间距 */
            border-top: 1px solid #eef2f7;
            padding-top: 10px;
            page-break-inside: avoid;
            page-break-before: auto;
            page-break-after: avoid;

        }


        .img-wrapper img {
            display: block;
            width: 100%;
            height: auto;
            border-radius: 4px;
            page-break-inside: avoid;
            page-break-after: avoid;
        }

        .img-wrapper img + img {
            margin-top: 2px;
        }

        .keep-together {
            page-break-inside: avoid;
        }

        /* ============================================================
               打印优化
               ============================================================ */
        @media print {
            body {
                background: #ffffff;
                padding: 0;
                margin: 0;
                display: block;
            }

            .page {
                width: 100%;
                min-height: auto;
                box-shadow: none;
                border-radius: 0;
                margin: 0;
                padding: 0;
                overflow: visible;
            }

            .cover-wrapper {
                height: 297mm;
                page-break-after: always;
                break-after: page;
                margin: 0;
                padding: 0;
            }

            .cover-page {
                height: 297mm;
                margin: 0;
                padding: 50px 40px;
            }

            .page-inner {
                padding: 6mm 12mm 8mm 12mm;
                page-break-before: auto;
                margin-top: 0;
            }

            .repayment-table-wrap {
                page-break-inside: avoid;
                page-break-after: auto;
            }



             .img-wrapper {
                page-break-inside: avoid;
                page-break-before: auto;
                page-break-after: avoid;
            }

            .section-title,
            .section-title-sm,
            .summary-text,
            .cost-table,
            .total-amount-box,
            .irr-box,
            .irr-result,
            .img-wrapper {
                page-break-inside: avoid;
                page-break-after: auto;
            }

            .img-responsive,
            .img-regulations {
                page-break-inside: avoid;
                page-break-after: avoid;
            }
        }

        .page-break,
        .page-break-before {
            display: none;
        }

        @media screen and (max-width: 800px) {
            body {
                padding: 10px;
            }
            .page {
                width: 100%;
                min-height: auto;
                border-radius: 0;
            }
            .cover-page {
                height: auto;
                min-height: 90vh;
                padding: 30px 20px;
            }
            .cover-page .report-title {
                font-size: 48px;
                color: #FF5900;
                letter-spacing: 4px;
                background: linear-gradient(0deg, #3071F3 0%, #3F9CEB 100%);
            }
            .repayment-table-wrap {
                overflow-x: auto;
            }
            .repayment-table-wrap table {
                font-size: 10px;
                min-width: 480px;
            }
        }

    </style>
</head>
<body>

<div class="page">

    <!-- ==========================================================
    封面
    ========================================================== -->
    <div class="cover-wrapper">
        <div class="cover-page">
            <div class="cover-title-wrapper">
                <div class="report-title">借款成本分析报告</div>
                <div class="report-sub-title">贷款成本一算通</div>
                <div class="report-date">报告日期：${reportDate!'2026年6月24日'}</div>
            </div>
        </div>
    </div>

    <!-- ==========================================================
    内容区域
    ========================================================== -->
    <div class="page-inner">

        <!-- 一、报告摘要 -->
        <span class="section-title">一、报告摘要</span>
        <p class="summary-text">
            根据您提供的贷款信息，经融算通系统精确计算，本笔贷款的借款成本近似年化利率约为
            <span class="highlight-red">${yearIrr!'36.5%'}</span>。
            该利率已超过国家规定的综合借款成本上限 <strong>24%</strong>，
            您可据此向平台或相关监管机构反馈该笔借款成本超限问题，以维护您的合法权利。
        </p>

        <span class="reg-link">相关监管规定</span>
        <div class="img-wrapper">
            <img class="img-regulations"
                 referrerpolicy="no-referrer"
                 src="https://lift-cloud.tos-cn-shanghai.volces.com/img/jianguan2.png"
                 alt="监管规定图示"/>
        </div>

        <!-- 二、贷款成本分析 -->
        <span class="section-title">二、贷款成本分析</span>
        <img class="img-chengben"
            src="${donutChartBase64!''}" alt="贷款成本分析"/>

       <!--<table class="cost-table">-->
       <!--    <tr>-->
       <!--        <td class="label-col">本金</td>-->
       <!--        <td class="value-col">${feeInfo.loanAmount!'10,000.00'} 元</td>-->
       <!--    </tr>-->
       <!--    <tr>-->
       <!--        <td class="label-col">息费</td>-->
       <!--        <td class="value-col">${feeInfo.interests!'1,200.00'} 元</td>-->
       <!--    </tr>-->
       <!--    <tr>-->
       <!--        <td class="label-col">会员费</td>-->
       <!--        <td class="value-col">${feeInfo.memberFee!'300.00'} 元</td>-->
       <!--    </tr>-->
       <!--</table>-->
       <!--<div class="total-amount-box">还款总额：${feeInfo.repayTotalAmout!'11,500.00'} 元</div>-->
       <!-- -->

        <!-- 真实借贷成本 -->
        <span class="section-title-sm">真实借贷成本</span>
        <div class="irr-box">
            <span class="irr-label">内部收益率（IRR）计算公式</span>
            <strong>t = 0</strong>：放款时点（实际到手资金 = 本金 − 一次性收取会员权益 ${feeInfo.memberFee!'300'} 元）<br/>
            <strong>C₁, C₂, ..., Cₙ</strong>：各期实际还款金额<br/>
            <strong>月 IRR</strong> 是使现金流净现值 <strong>NPV = 0</strong> 的折现率
        </div>
        <div class="img-wrapper">
            <img class="img-responsive"
                 referrerpolicy="no-referrer"
                 src="https://lift-cloud.tos-cn-shanghai.volces.com/img/irr.png"
                 alt="IRR公式图示"/>
        </div>
        <div class="irr-result">
            <img
                    class="image_4"
                    referrerpolicy="no-referrer"
                    src="https://lift-cloud.tos-cn-shanghai.volces.com/img/star.png"
            />
            <span>
            根据公式计算：月IRR ≈ <span class="highlight-blue">${monthIrr!'2.8%'}</span>，
            年化利率=月IRR × 12 ≈ <span class="highlight-red">${yearIrr!'36.5%'}</span>
                </span>
        </div>

        <!-- 三、应退款金额计算 -->
        <span class="section-title">三、应退款金额计算</span>
        <p class="desc-text">
            借款 <span style="color:#2980b9;font-weight:700;">${actualAmount!'10,000'}</span> 元，
            按国家规定的最高 <strong>24%</strong> 年利率重新计算每月应还款金额，
            每月超额部分即为应退款金额：
        </p>

        <!-- 还款计划表格 -->
        <div class="repayment-table-wrap keep-together">
            <table>
                <thead>
                <tr>
                    <th class="col-period">还款期次</th>
                    <th class="col-actual">实际还款金额（元）</th>
                    <th class="col-legal">24% 年利率应还（元）</th>
                    <th class="col-excess blue-bg">超额应退款（元）</th>
                </tr>
                </thead>
                <tbody>
                <#if lastLoanOrderList?? && lastLoanOrderList?size gt 0>
                <#list lastLoanOrderList as loan>
                <#assign rowIndex = loan?index>
                <tr class="${(rowIndex % 2 == 0)?then('', 'alt')}">
                    <td class="col-period">第 ${loan.installmentNo!'1'} 期</td>
                    <td class="col-actual">${loan.everyPlanActulAmount!'0.00'}</td>
                    <td class="col-legal">${loan.everyPlanAmount!'0.00'}</td>
                    <td class="col-excess blue-bg">${loan.backAmount!'0.00'}</td>
                </tr>
                </#list>
                <#else>
                <tr>
                    <td colspan="4" style="text-align:center;color:#9aabbe;padding:16px 0;">暂无还款数据</td>
                </tr>
                </#if>
            </tbody>
            <#if repayResult??>
            <tfoot>
                <tr>
                    <td class="col-period"><strong>合计</strong></td>
                    <td class="col-actual"><strong>${repayResult.totalPlanActulAmount!'0.00'}</strong></td>
                    <td class="col-legal"><strong>${repayResult.totalPlanAmount!'0.00'}</strong></td>
                    <td class="col-excess blue-bg"><strong>${repayResult.totalBackAmount!'0.00'}</strong></td>
                </tr>
            </tfoot>
            </#if>
        </table>
    </div>

    <!-- =======================================================
    底部图片
    ========================================================== -->

    <div class="img-wrapper">
        <img referrerpolicy="no-referrer"
             src="https://lift-cloud.tos-cn-shanghai.volces.com/img/Frame401.png"
             alt="底部图片1"/>
    </div>
    <div class="img-wrapper">
        <img referrerpolicy="no-referrer"
             src="https://lift-cloud.tos-cn-shanghai.volces.com/img/Frame402.png"
             alt="底部图片2"/>
    </div>
    <div class="img-wrapper">
        <img referrerpolicy="no-referrer"
             src="https://lift-cloud.tos-cn-shanghai.volces.com/img/Frame403.png"
             alt="底部图片3"/>
    </div>
    <div class="img-wrapper">
        <img referrerpolicy="no-referrer"
             src="https://lift-cloud.tos-cn-shanghai.volces.com/img/Frame404.png"
             alt="底部图片4"/>
    </div>
    <div class="img-wrapper">
        <img referrerpolicy="no-referrer"
             src="https://lift-cloud.tos-cn-shanghai.volces.com/img/Frame501.png"
             alt="底部图片5"/>
    </div>


</div><!-- end page-inner -->

</div><!-- end page -->

</body>
</html>