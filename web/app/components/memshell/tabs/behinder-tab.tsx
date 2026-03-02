import { Controller, type UseFormReturn } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Card, CardContent } from "@/components/ui/card";
import { Field, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { TabsContent } from "@/components/ui/tabs";
import type { MemShellFormSchema } from "@/types/schema";
import { OptionalClassFormField } from "./classname-field";
import { ShellTypeFormField } from "./shelltype-field";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";

export function BehinderTabContent({
  form,
  shellTypes,
}: Readonly<{
  form: UseFormReturn<MemShellFormSchema>;
  shellTypes: Array<string>;
}>) {
  const { t } = useTranslation(["memshell", "common"]);
  return (
    <TabsContent value="Behinder">
      <Card>
        <CardContent className="space-y-2 mt-4">
          <ShellTypeFormField form={form} shellTypes={shellTypes} />
          <Controller
            control={form.control}
            name="behinderProtocol"
            render={({ field }) => (
              <Field className="gap-2">
                <FieldLabel>
                  {t("shellToolConfig.behinder.protocol")}
                </FieldLabel>
                <RadioGroup
                  onValueChange={field.onChange}
                  value={field.value}
                  className="flex items-center gap-4"
                >
                  <div className="flex items-center gap-1">
                    <RadioGroupItem value="aes" id="aes" />
                    <Label htmlFor="aes">AES ({t("common:default")})</Label>
                  </div>
                  <div className="flex items-center gap-1">
                    <RadioGroupItem value="custom" id="custom" />
                    <Label htmlFor="custom">Custom (Gzip+Base64)</Label>
                  </div>
                </RadioGroup>
              </Field>
            )}
          />
          <Controller
            control={form.control}
            name="behinderPass"
            render={({ field }) => (
              <Field className="gap-1">
                <FieldLabel>
                  {t("shellToolConfig.behinder.pass")} {t("common:optional")}
                </FieldLabel>
                <Input
                  {...field}
                  placeholder={t("common:placeholders.input")}
                />
              </Field>
            )}
          />
          <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
            <Controller
              control={form.control}
              name="headerName"
              render={({ field }) => (
                <Field className="gap-1">
                  <FieldLabel>{t("common:headerName")}</FieldLabel>
                  <Input
                    {...field}
                    placeholder={t("common:placeholders.input")}
                  />
                </Field>
              )}
            />
            <Controller
              control={form.control}
              name="headerValue"
              render={({ field }) => (
                <Field className="gap-1">
                  <FieldLabel>
                    {t("common:headerValue")} {t("common:optional")}
                  </FieldLabel>
                  <Input
                    {...field}
                    placeholder={t("common:placeholders.input")}
                  />
                </Field>
              )}
            />
          </div>
          <OptionalClassFormField form={form} />
        </CardContent>
      </Card>
    </TabsContent>
  );
}
