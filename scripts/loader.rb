# load script to get MTA data into mongodb

require 'mongo'

if ARGV.length < 1
  puts "Usage: ruby loader.rb <input file>"
  exit
end

if infile = File.open(ARGV[0], "r")
  db = Mongo::Connection.new.db("mta")
  coll = db.collection("turnstyle")

  count = 0
  while line = infile.gets
    data = line.split(',').each{ |x| x.strip() }
    header = data.slice!(0..2)
    if data.length % 5 == 0
      while !data.empty?
        chunk = data.slice!(0..4)
        doc = { "area"  => header[0],
                "unit"  => header[1],
                "device"=> header[2],
                "date"  => data[0],
                "time"  => data[1],
                "in"    => data[3].to_i,
                "out"   => data[4].to_i }
        coll.insert(doc)
        count = count + 1
      end
    end
  end
  puts "wrote #{count} entries"
end
