/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package net.java.slee.resource.diameter.gx.events.avp;

import java.io.StreamCorruptedException;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the PDP-Session-Operation enumerated type.
 *
 * @author <a href="mailto:karthikeyan_s@spanservices.com"> Karthikeyan Shanmugam (EmblaCom)</a>
 */
public class PDPSessionOperation implements Enumerated, java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private int value = 0;
    public static final int PDP_SESSION_TERMINATION = 0;

    public static final PDPSessionOperation _PDP_SESSION_TERMINATION = new PDPSessionOperation(PDP_SESSION_TERMINATION);

    private PDPSessionOperation(int v) {
        value = v;
    }

    public int getValue() {
        return value;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static PDPSessionOperation fromInt(int type) {
        switch (type) {
            case PDP_SESSION_TERMINATION:
                return _PDP_SESSION_TERMINATION;

            default:
                throw new IllegalArgumentException("Invalid pdpSessionOperation value: " + type);
        }
    }

    private Object readResolve() throws StreamCorruptedException {
        try {
            return fromInt(value);
        } catch (IllegalArgumentException iae) {
            throw new StreamCorruptedException("Invalid internal state found: " + value);
        }
    }

    @Override
    public String toString() {
        switch (value) {
            case PDP_SESSION_TERMINATION:
                return "PDP_SESSION_TERMINATION";

            default:
                return "<Invalid Value>";
        }
    }



}
