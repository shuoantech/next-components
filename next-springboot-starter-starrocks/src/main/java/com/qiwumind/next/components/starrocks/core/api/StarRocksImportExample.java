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

package com.qiwumind.next.components.starrocks.core.api;



import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import com.qiwumind.next.components.starrocks.autoconfigure.StreamLoadConfigProperties;
import com.qiwumind.next.components.starrocks.core.api.handler.ContentStarRocksImporter;
import com.qiwumind.next.components.starrocks.core.dto.StreamLoadResponse;
import com.qiwumind.next.components.starrocks.core.infra.config.FELoadBalancer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用示例
 */
public class StarRocksImportExample {

    public static void main(String[] args) throws Exception {
        List<BigDecimal> list = new ArrayList<>();
        list.add(new BigDecimal("500"));
        list.add(new BigDecimal("1000"));
        BigDecimal stockAmt = list.stream()
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        String productCode = "MDZFB_" + stockAmt.intValue();
        System.out.println(productCode);
        // 1. 配置FE节点列表
        List<FELoadBalancer.FENode> feNodes = Arrays.asList(
                new FELoadBalancer.FENode("10.32.29.0", 8030, "FE-1", 1)
//            new FELoadBalancer.FENode("fe2.web.com", 8030, "FE-2", 2),
//            new FELoadBalancer.FENode("fe3.web.com", 8030, "FE-3", 3),
//            new FELoadBalancer.FENode("fe4.web.com", 8030, "FE-4", 1)
        );

        // 2. 创建负载均衡器
        FELoadBalancer loadBalancer = new FELoadBalancer(feNodes);
        loadBalancer.init();


        // 1. 创建导入配置
        String columns = "part_date, event, unique_id, user_id, event_time, distinct_id, server_time, timezone_offset, event_duration, H_app_version, H_app_id, H_app_name, H_is_first_day, H_is_first_time, H_lib, H_lib_version, H_lib_method, H_os, H_os_version, H_brand, H_model, H_manufacturer, H_device_id, H_anonymization_id, H_screen_height, H_screen_width, H_viewport_height, H_viewport_width, H_page_height, H_battery, H_totalSpace, H_residualSpace, H_memorySpace, H_sendSystem, H_network_type, H_is_wifi, H_carrier, H_country, H_province, H_city, H_ip, longitude, latitude, location_address, IP_address, H_screen_name, H_title, H_url, H_url_host, H_url_path, H_referrer, H_referrer_host, H_referrer_title, H_latest_referrer, page_name, page_title, page_url, H_element_content, H_element_position, H_element_id, H_element_type, H_element_selector, H_element_path, H_element_name, H_element_class_name, H_element_target_url, H_page_x, H_page_y, H_viewport_position, H_utm_source, H_utm_campaign, H_utm_medium, H_utm_term, H_utm_content, H_latest_utm_source, H_latest_utm_campaign, H_latest_utm_medium, H_latest_utm_term, H_latest_utm_content, H_latest_search_keyword, H_latest_traffic_source_type, login_start, login_start_time, login_leavetime, homepage_entrytime, homepage_leavetime, classify_entrytime, classify_leavetime, cart_entrytime, cart_leavetime, mine_entrytime, mine_leavetime, productlist_entrytime, productlist_leavetime, searchpage_entrytime, searchpage_leavetime, edit_page, edit_start_time, edit_stop_time, edit_name, edit_content, start_time, stop_time, click_time, pagestart_time, page_starttime, app_launch_time, open_time, order_time, pay_time, browse_time, record_time, trigger_time, exposure_time, open_interval, app_launch_interval, stay_time, stay_duration, duration, review_duration, login_duration, load_duration, app_start_duration, browse_source, browse_timemark, click_detail, click_info, button_name, button_value, button_text, click_type, cleck_page, cleck_time, cleck_event, cleck_description, key_word, search_defult_key, search_time, login_source, login_type, login_phone, first_login, regist_channel, apply_channel, read_operator_protocol, read_service_protocol, read_privacy_protocol, token_error_msg, slide_result, customer_id, customer_status, credit_status, product_id, product_name, product_price, product_category, product_categoryTwo, product_specs, product_spec, product_info, sku_id, PrimaryCategoryName, brand_name, brand_id, category_name, category_id, activity_name, home_activity_name, home_activity_id, activity_link, coupon_name, coupon_info, banner_id, banner_img, banner_url, ActivityName, TopicName, order_type, order_num, OrderNo, pay_amount, payment_amount, pay_result, payment_result, pay_results, card_state, cardTableId, installment_terms, credit_amount, default_period, payment_method, address_area, address_info, H_addressInfo, shipping_address, gift_name, goshop_guideUrl, channel_code, ProductID, CouponID, LightAssetOrgName, light_asset_org_name, LoanSuperProductName, TaskID, LOtteryPositionID, test_click_id, test_click_name, test_click_time, device_brand, H_is_login_id, onCreate";
        StreamLoadConfigProperties config = new StreamLoadConfigProperties.Builder()
                .maxFilterRatio(0.1)
                .columns(columns)
                .skipHeader(1)
                .build();

        // 2 创建Stream Loader
        ContentStarRocksImporter lbImporter = new ContentStarRocksImporter(loadBalancer,new StarRocksClusterProperties(),
                config);
        // 等待健康检查完成
        Thread.sleep(2000);
        // 4. 打印初始状态
        lbImporter.printLoadBalancerStatus();

        String csvUrl = "https://s3-model.hinadt.com/HinaCloudFilesTmp/dwd_event_WYSCIOS_90ed52f27f784da8-a3ec2bc69d5bea5d_0.csv";

        csvUrl = "https://s3-model.hinadt.com/HinaCloudFilesTmp/dwd_event_WYSCIOS_d82970646f9b436e-a4cce1cd0eae8bbe_0.csv";
        StreamLoadResponse singleResponse = lbImporter.importStarRocks(csvUrl, "user_behavior_evnets_detail");

        if (singleResponse.isSuccess()) {
            System.out.println(" 修复版导入成功! ");
            System.out.println("加载行数: " + singleResponse.getNumberLoadedRows());
        } else {
            System.err.println("修复版导入失败: " + BaseDTO.toJson(singleResponse));
        }
        lbImporter.printLoadBalancerStatus();

        lbImporter.close();
        loadBalancer.shutdown();

    }
}
