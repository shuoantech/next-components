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

package com.qiwumind.next.components.wechat.web;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qiwumind.next.components.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信菜单管理
 */
@Slf4j
@RestController
@RequestMapping("/api/wechat/menu")
@RequiredArgsConstructor
public class WechatMenuController {
    private final WxMpService wxMpService;

    /**
     * 发布自定义菜单
     * <p>
     * 支持两种方式：
     * 1. 传入完整JSON直接发布，后续考虑权限控制publish
     *
     * @param json 菜单JSON字符串（当menuId为空时使用）
     * @return 统一结果
     */
    @PostMapping("/publish")
    public Result publishMenu(@RequestBody(required = false) String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return Result.fail("PARAM_ERROR", "菜单JSON不能为空");
            }
            // 1. 使用 fastjson 解析 JSON
            JSONObject jsonObject = JSON.parseObject(json);

            JSONArray buttonArray = jsonObject.getJSONArray("button");
            if (buttonArray == null || buttonArray.isEmpty()) {
                return Result.fail("INVALID_JSON", "菜单至少需要1个一级菜单");
            }

            // 2. 手动构建 WxMenu 对象
            WxMenu menu = new WxMenu();
            for (int i = 0; i < buttonArray.size(); i++) {
                JSONObject btnObj = buttonArray.getJSONObject(i);
                WxMenuButton button = parseButton(btnObj);
                menu.getButtons().add(button);
            }

            // 4. 校验菜单规则
            String validateResult = validateMenu(menu);
            if (validateResult != null) {
                return Result.fail("VALIDATE_ERROR", validateResult);
            }
            log.error("菜单发布: {}", menu);
            wxMpService.getMenuService().menuCreate(menu);
            return Result.success();

        } catch (Exception e) {
            log.error("菜单发布异常", e);
            return Result.fail("SYSTEM_ERROR", "系统异常：" + e.getMessage());
        }
    }

    /**
     * 递归解析按钮
     */
    private WxMenuButton parseButton(JSONObject btnObj) {
        WxMenuButton button = new WxMenuButton();
        button.setName(btnObj.getString("name"));
        button.setType(btnObj.getString("type"));
        button.setUrl(btnObj.getString("url"));
        button.setKey(btnObj.getString("key"));
        button.setAppId(btnObj.getString("appid"));
        button.setPagePath(btnObj.getString("pagepath"));

        // 处理子菜单
        JSONArray subButtons = btnObj.getJSONArray("sub_button");
        if (subButtons != null && !subButtons.isEmpty()) {
            for (int i = 0; i < subButtons.size(); i++) {
                JSONObject subObj = subButtons.getJSONObject(i);
                WxMenuButton subButton = parseButton(subObj);
                button.getSubButtons().add(subButton);
            }
        }

        return button;
    }

    /**
     * 删除自定义菜单
     */
    @DeleteMapping("/delete")
    public Result deleteMenu(@RequestParam(required = false) String menuId) {
        boolean success = false;
        try {
            if (StringUtils.isBlank(menuId)) {
                wxMpService.getMenuService().menuDelete();
            } else {
                wxMpService.getMenuService().menuDelete(menuId);
            }
            log.info("自定义菜单删除成功");
            success = true;
        } catch (Exception e) {
            log.error("自定义菜单删除失败: {}", e.getMessage(), e);

        }
        return success ? Result.success() : Result.fail("999999", "菜单删除失败");
    }

    /**
     * 校验菜单业务规则
     */
    private String validateMenu(WxMenu menu) {
        List<WxMenuButton> buttons = menu.getButtons();

        // 一级菜单数量限制：1-3个
        if (buttons == null || buttons.isEmpty()) {
            return "菜单至少需要1个一级菜单";
        }
        if (buttons.size() > 3) {
            return "一级菜单不能超过3个，当前为" + buttons.size() + "个";
        }

        // 检查子菜单数量
        for (WxMenuButton button : buttons) {
            List<WxMenuButton> subButtons = button.getSubButtons();
            if (subButtons != null && subButtons.size() > 5) {
                return "子菜单不能超过5个，菜单【" + button.getName() + "】有" + subButtons.size() + "个子菜单";
            }
            // 父级菜单有子菜单时，不能设置type/url/key
            if (subButtons != null && !subButtons.isEmpty()) {
                if (button.getType() != null) {
                    return "有子菜单的【" + button.getName() + "】不能设置type属性";
                }
            }
        }
        return null;
    }


}