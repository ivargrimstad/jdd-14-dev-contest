package pl.allegro.jdd;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.TreeTraverser;
import static java.util.stream.Collectors.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureDiff {

   public Changes calculate(Employee oldCTO, Employee newCTO) {
      checkNotNull(oldCTO);
      checkNotNull(newCTO);

      TreeTraverser<Employee> treeTraverser = new TreeTraverser<Employee>() {
         @Override
         public Iterable<Employee> children(Employee employee) {
            return employee.getSubordinates();
         }
      };

      final Set<Employee> oldies = treeTraverser.breadthFirstTraversal(oldCTO).toSet();
      final Set<Employee> newbies = treeTraverser.breadthFirstTraversal(newCTO).toSet();

      final List<Employee> fired = oldies.stream()
              .filter(oldE -> !newCTO.getSubordinates().contains(oldE) && !oldE.equals(newCTO))
              .collect(toList());

      final List<Employee> changedSalary = new ArrayList<>();
      oldies.stream()
              .forEach(o -> {
                 newbies.stream().filter(n -> o.getSalary() != n.getSalary()).forEach(i -> {
                    changedSalary.add(o);
                 });
              });

      final List<Employee> hired = newbies.stream()
              .filter(newE -> !oldies.contains(newE))
              .collect(toList());

      return new Changes(fired, hired, changedSalary);
   }
}
