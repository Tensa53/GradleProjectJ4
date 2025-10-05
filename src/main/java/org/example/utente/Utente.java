package org.example.utente;

import org.example.banca.ContoBancario;

import java.util.ArrayList;
import java.util.Set;

public class Utente {

    public Utente(String name, String surname, String telephone, String address, ContoBancario contoBancario) {
        this.name = name;//aggiungo commento
        this.surname = surname;//aggiunto altro commento
        this.telephone = telephone;
        this.address = address;
        this.contoBancario = contoBancario;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAddress(int a) {
        return address;
    }

    public ContoBancario getContoBancario() {
        return contoBancario;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setContoBancario(ContoBancario contoBancario) {
        this.contoBancario = contoBancario;
    }

    public void prova(ArrayList<String> prova, Set<String> provare) {
        prova.add(name);
        prova.add(surname);
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String name;
    private String surname;
    private String telephone;
    private String address;
    private ContoBancario contoBancario;
}
