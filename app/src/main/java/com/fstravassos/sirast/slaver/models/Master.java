package com.fstravassos.sirast.slaver.models;

/**
 * Created by felip_000 on 14/02/2017.
 */

public class Master {
        private int Id;
        private String Name;
        private String Number;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String number) {
            Number = number;
        }
}
