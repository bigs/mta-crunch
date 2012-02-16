function map() {
  emit(this.area + "|" +
       this.unit + "|" +
       this.time,
       {in: this.in,
        out: this.out});
}

function reduce(key, values) {
  result = {in: 0, out: 0};
  count = 0;
  values.forEach(function(value) {
    result.in += value.in;
    result.out += value.out;
    count++;
  });
  result.in /= count;
  result.out /= count;
  return result;
}

db.runCommand(
    { mapreduce : "turnstyle",
      map : map,
      reduce : reduce,
      out: { replace : "turn_time" }});
