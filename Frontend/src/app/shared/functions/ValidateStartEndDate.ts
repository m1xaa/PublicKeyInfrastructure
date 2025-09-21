import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';


export function futureDateValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null; 
  const today = new Date();
  today.setHours(0, 0, 0, 0); 
  const selected = new Date(control.value);
  return selected > today ? null : { notFutureDate: true };
}
export function futureDateRangeValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const start = group.get('validFrom')?.value;
    const end = group.get('validTo')?.value;

    if (start) {
      const startDate = new Date(start);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (startDate <= today) {
        return { startNotFuture: true };
      }
    }

    if (end) {
      const endDate = new Date(end);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (endDate <= today) {
        return { endNotFuture: true };
      }
    }

    if (start && end) {
      const startDate = new Date(start);
      const endDate = new Date(end);
      if (endDate <= startDate) {
        return { endBeforeStart: true };
      }
    }

    return null;
  };
}
