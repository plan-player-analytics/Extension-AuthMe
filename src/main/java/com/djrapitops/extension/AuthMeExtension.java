/*
    Copyright(c) 2021 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.api.v3.AuthMePlayer;

import java.time.Instant;
import java.util.ArrayList;

/**
 * DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "AuthMe", iconName = "key", iconFamily = Family.SOLID, color = Color.AMBER)
public class AuthMeExtension implements DataExtension {

    private AuthMeApi api;

    public AuthMeExtension() {
        api = AuthMeApi.getInstance();
    }

    public AuthMeExtension(boolean forTesting) {}

    @DataBuilderProvider
    public ExtensionDataBuilder playerData(String playerName) {
        AuthMePlayer player = api.getPlayerInfo(playerName).orElseThrow(NotReadyException::new);

        String altsFromRegisterIP = player.getRegistrationIpAddress().map(api::getNamesByIp).orElseGet(ArrayList::new).toString();
        String altsFromLastJoinIP = player.getLastLoginIpAddress().map(api::getNamesByIp).orElseGet(ArrayList::new).toString();

        long lastLogin = player.getLastLoginDate().map(Instant::getEpochSecond).orElse(0L) * 1000L;

        return newExtensionDataBuilder()
                .addValue(String.class, valueBuilder("Alts using register IP")
                        .priority(100)
                        .icon(Icon.called("users").of(Color.LIGHT_GREEN).build())
                        .buildString(altsFromRegisterIP.substring(1, altsFromRegisterIP.length() - 1)))
                .addValue(String.class, valueBuilder("Alts using last join IP")
                        .priority(90)
                        .icon(Icon.called("users").of(Color.LIGHT_BLUE).build())
                        .buildString(altsFromLastJoinIP.substring(1, altsFromLastJoinIP.length() - 1)))
                .addValue(Long.class, valueBuilder("Last Login")
                        .priority(80)
                        .icon(Icon.called("calendar").of(Family.REGULAR).of(Color.LIGHT_BLUE).build())
                        .formatAsDateWithSeconds()
                        .buildNumber(lastLogin));
    }
}