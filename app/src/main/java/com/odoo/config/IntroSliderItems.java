/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 13/2/15 4:16 PM
 */
package com.odoo.config;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.odoo.R;
import com.odoo.core.support.list.OListAdapter;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.appintro.SliderItem;
import odoo.controls.appintro.SliderPagerAdapter;

public class IntroSliderItems implements SliderPagerAdapter.SliderBuilderListener {
    public static final String TAG = IntroSliderItems.class.getSimpleName();

    public IntroSliderItems(Bundle extras) {
    }

    public List<SliderItem> getItems() {
        List<SliderItem> items = new ArrayList<>();
//        items.add(new SliderItem("Odoo Experience",
//                "More then 2500+ Professionals at one place", R.drawable.s001, this));
//        items.add(new SliderItem("120+ Sessions",
//                "Discover Odoo and how we can help you grow your business", R.drawable.s002, this));
//        items.add(new SliderItem("20+ exhibitors",
//                "Join us and learn from exhibitors about their achievements success stories and brilliant ideas for improvement", R.drawable.s003, this));
//        items.add(new SliderItem("Meet Customers and Partners",
//                "More than 400 partners from  all over the world will be there to showcase their modules share their experience and learn from each other", R.drawable.s004, this));
//        items.add(new SliderItem("Training Sessions",
//                "Advanced training sessions are organized before the event", R.drawable.s005, this));
//        items.add(new SliderItem("Developer Session",
//                "This developers sessions will help you to explore advanced subjects related to the development of Odoo modules", R.drawable.s006, this));
//        items.add(new SliderItem("An Open Spirit",
//                "Whether it's during the workshops or the evening beer events around 2000 people will be there sharing knowledge their experience and best practices", R.drawable.s007, this));
//        items.add(new SliderItem("Testimonials from last Odoo Experience ", "", R.drawable.s008, this)
//                .putExtra("is_grid", true));
//        items.add(new SliderItem("Manage your track easily with Odoo Experience Mobile App", "", R.drawable.s009, this)
//                .putExtra("is_launch_screen", true));
        return items;
    }

    @Override
    public View getCustomView(final Context context, SliderItem item, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_intro_slider_view, parent, false);
        OControls.setImage(view, R.id.backgroundImage, item.getImagePath());
        if (item.getExtras().containsKey("is_grid")) {
            bindGrid(context, view);
            OControls.setGone(view, R.id.contentView);
            OControls.setGone(view, R.id.launchContainer);
            OControls.setVisible(view, R.id.gridContainer);
            OControls.setText(view, R.id.bigTitle, item.getTitle());
        } else if (item.getExtras().containsKey("is_launch_screen")) {
            OControls.setGone(view, R.id.contentView);
            OControls.setVisible(view, R.id.launchContainer);
            OControls.setGone(view, R.id.gridContainer);
            OControls.setText(view, R.id.big_Title, item.getTitle());
            view.findViewById(R.id.letsExplore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).finish();
                }
            });
        } else {
            OControls.setVisible(view, R.id.contentView);
            OControls.setGone(view, R.id.launchContainer);
            OControls.setGone(view, R.id.gridContainer);
            OControls.setText(view, R.id.big_title, item.getTitle());
            OControls.setText(view, R.id.description, item.getContent());
        }
        return view;
    }

    private void bindGrid(final Context context, View view) {
        GridView grid = (GridView) view.findViewById(R.id.gridView);
        List<Object> items = new ArrayList<>();
        items.addAll(getTestimonials());
        OListAdapter listAdapter = new OListAdapter(context, R.layout.testomonial_view, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = LayoutInflater.from(context).inflate(getResource(), parent, false);
                }
                TestimonialItem item = (TestimonialItem) getItem(position);
                OControls.setText(view, R.id.testimonialAuthor, item.getAuthor());
                OControls.setText(view, R.id.testimonialContent, item.getContent());
                return view;
            }
        };
        grid.setAdapter(listAdapter);
    }

    private List<TestimonialItem> getTestimonials() {
        List<TestimonialItem> items = new ArrayList<>();
        items.add(new TestimonialItem("Bertrand Hanot, BHC Belgium ",
                "Thanks again for this beautiful event! We learnt so much in three days. "));
        items.add(new TestimonialItem("Jonathan Wilson, WillowIT Australia ",
                "All the OpenERP staff members I have been in contact with have been polite, helpful, knowledgeable, and passionate, which are all essential qualities for success."));
        items.add(new TestimonialItem("Antoine Huvelle, Belgium",
                "Congratulations for the event. I've been participating in the three last Community Days and I must say that this one was by far the most professional and interesting."));
        items.add(new TestimonialItem("Josean Soroa, Soroatic Spain ",
                "(…) And please give my thanks to all OpenERP team. You have been really kind, everything was very well organized and the interest of sessions was very high."));
        items.add(new TestimonialItem("Coralie Girardet, Audaxis France",
                "The event program was really interesting and the organization was just perfect congratulations to all of you..."));
        items.add(new TestimonialItem("Nicolas Rigo, Eezee-It",
                "Can't wait for Odoo Experience 2016 edition. Thank you for the great moment!"));
        items.add(new TestimonialItem("Maxime Chambreuil, Savoirfairelinux Canada ",
                "Thank you very much and congrats for the organization!"));
        items.add(new TestimonialItem("Leonardo Pistone, AgileGroup Italy",
                "Went smooth as silk"));
        items.add(new TestimonialItem("Frederick Van Der Essen ",
                "And a big thank you to you two, I think everybody could see how nice the organization was :)"));
        return items;
    }

    public class TestimonialItem {
        String content, author;

        public TestimonialItem(String author, String content) {
            this.author = author;
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
