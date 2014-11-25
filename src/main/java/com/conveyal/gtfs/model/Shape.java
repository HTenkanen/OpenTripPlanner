/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.util.Iterator;

import org.mapdb.Fun.Tuple2;

public class Shape extends Entity {

    public String shape_id;
    public double shape_pt_lat;
    public double shape_pt_lon;
    public int    shape_pt_sequence;
    public double shape_dist_traveled;

    public static class Loader extends Entity.Loader<Shape> {

        public Loader(GTFSFeed feed) {
            super(feed, "shapes");
        }

        @Override
        public void loadOneRow() throws IOException {
            Shape s = new Shape();
            s.shape_id = getStringField("shape_id", true);
            s.shape_pt_lat = getDoubleField("shape_pt_lat", true, -90D, 90D);
            s.shape_pt_lon = getDoubleField("shape_pt_lon", true, -180D, 180D);
            s.shape_pt_sequence = getIntField("shape_pt_sequence", true, 0, Integer.MAX_VALUE);
            s.shape_dist_traveled = getDoubleField("shape_dist_traveled", false, 0D, Double.MAX_VALUE);
            s.feed = null; // since we're putting this into MapDB, we don't want circular serialization
            feed.shapePoints.put(new Tuple2<String, Integer>(s.shape_id, s.shape_pt_sequence), s);
            
            if (!feed.shapes.containsKey(s.shape_id)) {
            	feed.shapes.put(s.shape_id, new ShapeMap(feed.shapePoints, s.shape_id));
            }
        }

    }
    
    public static class Writer extends Entity.Writer<Shape> {
    	public Writer (GTFSFeed feed) {
    		super(feed, "shapes");
    	}

		@Override
		protected void writeHeaders() throws IOException {
			writer.writeRecord(new String[] {"shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled"});
		}

		@Override
		protected void writeOneRow(Shape s) throws IOException {
			writeStringField(s.shape_id);
			writeDoubleField(s.shape_pt_lat);
			writeDoubleField(s.shape_pt_lon);
			writeIntField(s.shape_pt_sequence);
			writeDoubleField(s.shape_dist_traveled);
			endRecord();
		}

		@Override
		protected Iterator<Shape> iterator() {
			return feed.shapePoints.values().iterator();
		}
    	
    	
    }
}