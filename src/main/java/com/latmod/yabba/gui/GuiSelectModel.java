package com.latmod.yabba.gui;

import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.latmod.yabba.client.BarrelModel;
import com.latmod.yabba.net.MessageSelectModel;
import com.latmod.yabba.util.EnumBarrelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiSelectModel extends GuiBase
{
	private class ButtonModel extends Button
	{
		private final BarrelModel model;
		private final int index;

		public ButtonModel(Panel panel, int i)
		{
			super(panel);
			setPosAndSize(8 + (i % 5) * 39, 8 + (i / 5) * 39, 38, 38);
			index = i;
			model = index >= EnumBarrelModel.NAME_MAP.size() ? null : EnumBarrelModel.NAME_MAP.get(index).getModel();

			if (model != null)
			{
				setTitle(model.toString());
			}
			else
			{
				setIcon(getTheme().getButton(WidgetType.DISABLED));
			}
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (model != null)
			{
				GuiHelper.playClickSound();
				new MessageSelectModel(model.id).sendToServer();
				getGui().closeGui();
			}
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();
			getIcon().draw(ax, ay, width, height);

			if (model != null)
			{
				model.icon.draw(ax + 3, ay + 3, 32, 32);
			}
		}
	}

	private final List<ButtonModel> buttons;

	public GuiSelectModel()
	{
		setSize(210, 171);

		buttons = new ArrayList<>();

		for (int i = 0; i < 20; i++)
		{
			buttons.add(new ButtonModel(this, i));
		}
	}

	@Override
	public void addWidgets()
	{
		addAll(buttons);
	}
}