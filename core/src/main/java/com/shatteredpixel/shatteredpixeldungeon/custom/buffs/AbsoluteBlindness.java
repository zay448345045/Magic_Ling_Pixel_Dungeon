package com.shatteredpixel.shatteredpixeldungeon.custom.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class AbsoluteBlindness extends Buff {

    {
        // 设置buff的行为优先级、是否公告以及类型
        actPriority = VFX_PRIO;
        announced = true;
        type = buffType.NEGATIVE;
    }

    // 保存剩余时间
    protected float left = 0f;
    // 保存目标之前的视野距离
    private int storedViewDistance;

    @Override
    public boolean act() {
        // 消耗一个游戏tick的时间
        spend(TICK);

        // 更新存储的视野距离（如果当前视野距离大于0且与存储的不同）
        if (target.viewDistance > 0 &&storedViewDistance != target.viewDistance) {
            storedViewDistance = target.viewDistance;
        }

        // 将目标的视野距离设置为0，模拟失明效果
        target.viewDistance = 0;

        // 减少剩余时间
        left -= 1f;

        // 如果剩余时间小于0，则移除该buff
        if (left < 0) {
            detach();
        }

        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        // 保存存储的视野距离和剩余时间
        bundle.put("storedVD", storedViewDistance);
        bundle.put("blindLeft", left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        // 从bundle中恢复存储的视野距离和剩余时间
        storedViewDistance = bundle.getInt("storedVD");
        left = bundle.getFloat("blindLeft") + 1f;
    }

    // 增加剩余时间
    public AbsoluteBlindness addLeft(float left) {
        this.left += left;
        return this;
    }

    @Override
    public void detach() {
        // 恢复目标的视野距离
        target.viewDistance = Dungeon.level.viewDistance;
        // 触发观察事件
        Dungeon.observe();
        // 调用父类的detach方法
        super.detach();
    }

    @Override
    public int icon() {
        // 返回失明状态的图标
        return BuffIndicator.BLINDNESS;
    }

    @Override
    public void tintIcon(Image icon) {
        // 设置图标的颜色
        icon.hardlight(0x3366D4);
    }

    @Override
    public String toString() {
        // 返回buff的名称
        return M.L(this, "name");
    }

    @Override
    public String heroMessage() {
        // 返回英雄的消息
        return M.L(this, "heromsg");
    }

    @Override
    public String desc() {
        // 返回buff的描述
        return M.L(this, "desc");
    }
}