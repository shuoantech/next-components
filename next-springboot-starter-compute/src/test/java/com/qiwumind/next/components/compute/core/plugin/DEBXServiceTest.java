/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.compute.core.plugin;

import com.qiwumind.next.components.compute.core.dto.ComputeSimpleReq;
import com.qiwumind.next.components.compute.core.dto.ComputeSimpleResp;
import com.qiwumind.next.components.compute.core.enums.RateEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import com.qiwumind.next.components.common.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class DEBXServiceTest {
    DEBXService DEBXService = new DEBXService();

//    @Test
    void calcCore() {
        ComputeSimpleReq req = new ComputeSimpleReq();
        req.setAmount(new BigDecimal("10000"));
        req.setRate(new BigDecimal("0.24"));
        req.setTrialDate(new Date());
        req.setRateType(RateEnum.YEAR_RATE);
        req.setInstallmentNo(12);
        req.setRepayWayEnum(RepayWayEnum.EQUAL_INSTALLMENT);
        req.setInstallmentStep("1M");
        ComputeSimpleResp resp = new ComputeSimpleResp();
        resp.setTrialDate(new Date());

        ArrayList<ComputeSimpleResp.SimpleInstallmentData> list = DEBXService.calc(req, resp);
        System.out.println(BaseDTO.toJson(list));

        List<BigDecimal> everyPlanToltalAmount = list.stream().map(s -> s.getPrincipal().add(s.getInterest())).collect(Collectors.toList());
        IrrRateComputeService irrRateComputeService = new IrrRateComputeService();
        BigDecimal calaulate = irrRateComputeService.calculate(new BigDecimal("10000"), everyPlanToltalAmount);
        System.out.println("calaulate=" + calaulate);

        AprRateComputeService aprRateComputeService=new AprRateComputeService();
        BigDecimal calaulate2 = aprRateComputeService.calculate(new BigDecimal("10000"), everyPlanToltalAmount);
        System.out.println("calaulate2=" + calaulate2);



        List<BigDecimal> everyPlanToltalAmount3 = new ArrayList<>();
        everyPlanToltalAmount3.add(new BigDecimal("1300"));
        everyPlanToltalAmount3.add(new BigDecimal("1300"));
        everyPlanToltalAmount3.add(new BigDecimal("1300"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));
        everyPlanToltalAmount3.add(new BigDecimal("1200"));

        BigDecimal calaulate3 = irrRateComputeService.calculate(new BigDecimal("10000"), everyPlanToltalAmount3);
        System.out.println("calaulate3=" + calaulate3);

    }





}
